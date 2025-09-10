import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Typography,
  Alert,
  Snackbar
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  CloudUpload,
  Archive,
  Star,
  StarBorder,
  Search
} from '@mui/icons-material';
import { articleApi, Article } from '../../services/api';

interface ArticleManagementProps {
  onCreateNew?: () => void;
  onEdit?: (article: Article) => void;
}

const ArticleManagement: React.FC<ArticleManagementProps> = ({ onCreateNew, onEdit }) => {
  const [articles, setArticles] = useState<Article[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [selectedStatus, setSelectedStatus] = useState('all');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [deleteDialog, setDeleteDialog] = useState<{ open: boolean; article: Article | null }>({
    open: false,
    article: null
  });
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({
    open: false,
    message: '',
    severity: 'success'
  });

  useEffect(() => {
    loadArticles();
  }, [page, rowsPerPage, selectedStatus, selectedCategory, searchKeyword]);

  const loadArticles = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: rowsPerPage,
        ...(selectedStatus !== 'all' && { status: selectedStatus }),
        ...(selectedCategory !== 'all' && { category: selectedCategory }),
        ...(searchKeyword && { search: searchKeyword })
      };

      const response = await articleApi.getArticles(params);
      setArticles(response.content);
      setTotalCount(response.totalElements);
      
    } catch (error) {
      console.error('Failed to load articles:', error);
      showSnackbar('게시글을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleStatusChange = async (article: Article, newStatus: string) => {
    try {
      const updatedArticle = {
        ...article,
        status: newStatus as 'PUBLIC' | 'PRIVATE'
      };
      await articleApi.updateArticle(article.id, updatedArticle);
      
      const message = newStatus === 'PUBLIC' ? '게시글이 공개되었습니다.' : '게시글이 비공개로 변경되었습니다.';
      showSnackbar(message, 'success');
      loadArticles();
    } catch (error) {
      console.error('Status change error:', error);
      showSnackbar('상태 변경 중 오류가 발생했습니다.', 'error');
    }
  };

  const handleToggleFeature = async (article: Article) => {
    try {
      await articleApi.toggleFeatured(article.id);
      showSnackbar(`주요 뉴스 설정이 ${article.featured ? '해제' : '설정'}되었습니다.`, 'success');
      loadArticles();
    } catch (error) {
      console.error('Toggle featured error:', error);
      showSnackbar('주요 뉴스 설정 중 오류가 발생했습니다.', 'error');
    }
  };

  const handleDelete = async () => {
    if (!deleteDialog.article) return;
    
    try {
      await articleApi.deleteArticle(deleteDialog.article.id);
      showSnackbar('게시글이 삭제되었습니다.', 'success');
      setDeleteDialog({ open: false, article: null });
      loadArticles();
    } catch (error) {
      console.error('Delete error:', error);
      showSnackbar('게시글 삭제 중 오류가 발생했습니다.', 'error');
    }
  };

  const showSnackbar = (message: string, severity: 'success' | 'error') => {
    setSnackbar({ open: true, message, severity });
  };

  const getStatusLabel = (status: string) => {
    const labels = {
      'PUBLIC': '공개',
      'PRIVATE': '비공개'
    };
    return labels[status as keyof typeof labels] || status;
  };

  const getStatusColor = (status: string) => {
    const colors = {
      'PUBLIC': 'success' as const,
      'PRIVATE': 'default' as const
    };
    return colors[status as keyof typeof colors] || 'default' as const;
  };

  const getCategoryLabel = (category: string) => {
    const labels = {
      'NEWS': '뉴스',
      'ANNOUNCEMENT': '공지사항',
      'PRODUCT_INFO': '제품정보',
      'UPDATE': '업데이트',
      'EVENT': '이벤트'
    };
    return labels[category as keyof typeof labels] || category;
  };

  const getCategoryColor = (category: string) => {
    const colors = {
      'NEWS': 'primary' as const,
      'ANNOUNCEMENT': 'error' as const,
      'PRODUCT_INFO': 'info' as const,
      'UPDATE': 'warning' as const,
      'EVENT': 'secondary' as const
    };
    return colors[category as keyof typeof colors] || 'default' as const;
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
            뉴스 및 공지사항 관리
          </Typography>
          <Button 
            startIcon={<Add />}
            variant="contained"
            sx={{ 
              backgroundColor: '#b8191c',
              '&:hover': { backgroundColor: '#a01018' }
            }}
            onClick={onCreateNew}
          >
            새 게시글 작성
          </Button>
        </Box>

        {/* 필터링 및 검색 */}
        <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>상태</InputLabel>
            <Select 
              value={selectedStatus} 
              onChange={(e) => setSelectedStatus(e.target.value)}
              label="상태"
            >
              <MenuItem value="all">전체</MenuItem>
              <MenuItem value="PUBLIC">공개</MenuItem>
              <MenuItem value="PRIVATE">비공개</MenuItem>
            </Select>
          </FormControl>

          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>카테고리</InputLabel>
            <Select 
              value={selectedCategory} 
              onChange={(e) => setSelectedCategory(e.target.value)}
              label="카테고리"
            >
              <MenuItem value="all">전체</MenuItem>
              <MenuItem value="NEWS">뉴스</MenuItem>
              <MenuItem value="ANNOUNCEMENT">공지사항</MenuItem>
              <MenuItem value="PRODUCT_INFO">제품정보</MenuItem>
              <MenuItem value="UPDATE">업데이트</MenuItem>
              <MenuItem value="EVENT">이벤트</MenuItem>
            </Select>
          </FormControl>

          <TextField 
            size="small" 
            placeholder="제목, 내용 검색..." 
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            sx={{ minWidth: 200 }}
            InputProps={{
              startAdornment: <Search sx={{ mr: 1, color: 'text.secondary' }} />
            }}
          />
        </Box>

        {/* 게시글 테이블 */}
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>제목</TableCell>
                <TableCell>카테고리</TableCell>
                <TableCell>상태</TableCell>
                <TableCell>작성자</TableCell>
                <TableCell>작성일</TableCell>
                <TableCell>조회수</TableCell>
                <TableCell>좋아요</TableCell>
                <TableCell>주요뉴스</TableCell>
                <TableCell align="center">액션</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {articles.map((article) => (
                <TableRow key={article.id} hover>
                  <TableCell>
                    <Box>
                      <Typography variant="body2" sx={{ fontWeight: 'medium' }}>
                        {article.title}
                      </Typography>
                      {article.summary && (
                        <Typography variant="caption" color="text.secondary">
                          {article.summary}
                        </Typography>
                      )}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={getCategoryLabel(article.category)}
                      size="small"
                      color={getCategoryColor(article.category)}
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={getStatusLabel(article.status)}
                      size="small"
                      color={getStatusColor(article.status)}
                    />
                  </TableCell>
                  <TableCell>{article.author}</TableCell>
                  <TableCell>
                    {new Date(article.createdAt).toLocaleDateString('ko-KR')}
                  </TableCell>
                  <TableCell>{article.viewsCount?.toLocaleString()}</TableCell>
                  <TableCell>{article.likesCount}</TableCell>
                  <TableCell>
                    <IconButton 
                      size="small" 
                      onClick={() => handleToggleFeature(article)}
                      color={article.featured ? 'warning' : 'default'}
                    >
                      {article.featured ? <Star /> : <StarBorder />}
                    </IconButton>
                  </TableCell>
                  <TableCell align="center">
                    <Box sx={{ display: 'flex', gap: 0.5 }}>
                      <IconButton size="small" color="primary" onClick={() => onEdit?.(article)}>
                        <Edit />
                      </IconButton>
                      
                      {article.status === 'PRIVATE' && (
                        <IconButton 
                          size="small" 
                          color="success"
                          onClick={() => handleStatusChange(article, 'PUBLIC')}
                          title="공개로 변경"
                        >
                          <CloudUpload />
                        </IconButton>
                      )}
                      
                      {article.status === 'PUBLIC' && (
                        <IconButton 
                          size="small" 
                          color="warning"
                          onClick={() => handleStatusChange(article, 'PRIVATE')}
                          title="비공개로 변경"
                        >
                          <Archive />
                        </IconButton>
                      )}
                      
                      <IconButton 
                        size="small" 
                        color="error"
                        onClick={() => setDeleteDialog({ open: true, article })}
                      >
                        <Delete />
                      </IconButton>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        {/* 페이지네이션 */}
        <TablePagination
          component="div"
          count={totalCount}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          rowsPerPageOptions={[5, 10, 25]}
          labelRowsPerPage="페이지당 행 수:"
          labelDisplayedRows={({ from, to, count }) => `${from}-${to} / 총 ${count}개`}
        />

        {/* 삭제 확인 다이얼로그 */}
        <Dialog
          open={deleteDialog.open}
          onClose={() => setDeleteDialog({ open: false, article: null })}
        >
          <DialogTitle>게시글 삭제</DialogTitle>
          <DialogContent>
            <Typography>
              "{deleteDialog.article?.title}" 게시글을 삭제하시겠습니까?
            </Typography>
            <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
              이 작업은 되돌릴 수 없습니다.
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDeleteDialog({ open: false, article: null })}>
              취소
            </Button>
            <Button onClick={handleDelete} color="error" variant="contained">
              삭제
            </Button>
          </DialogActions>
        </Dialog>

        {/* 스낵바 */}
        <Snackbar
          open={snackbar.open}
          autoHideDuration={4000}
          onClose={() => setSnackbar({ ...snackbar, open: false })}
        >
          <Alert 
            severity={snackbar.severity} 
            onClose={() => setSnackbar({ ...snackbar, open: false })}
          >
            {snackbar.message}
          </Alert>
        </Snackbar>
      </CardContent>
    </Card>
  );
};

export default ArticleManagement;