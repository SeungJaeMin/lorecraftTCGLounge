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
  IconButton,
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
  Snackbar,
  Menu,
  MenuItem
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  MoreVert,
  Refresh,
  CleaningServices
} from '@mui/icons-material';

interface Tag {
  id: number;
  name: string;
  slug: string;
  description?: string;
  color?: string;
  usageCount: number;
  createdAt: string;
}

interface TagManagementProps {
  onClose?: () => void;
}

const TagManagement: React.FC<TagManagementProps> = ({ onClose }) => {
  const [tags, setTags] = useState<Tag[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [searchKeyword, setSearchKeyword] = useState('');
  
  const [editDialog, setEditDialog] = useState<{ 
    open: boolean; 
    tag: Tag | null; 
    isNew: boolean;
  }>({
    open: false,
    tag: null,
    isNew: false
  });
  
  const [deleteDialog, setDeleteDialog] = useState<{ 
    open: boolean; 
    tag: Tag | null;
  }>({
    open: false,
    tag: null
  });

  const [menuAnchor, setMenuAnchor] = useState<{ 
    element: HTMLElement | null; 
    tag: Tag | null;
  }>({
    element: null,
    tag: null
  });

  const [snackbar, setSnackbar] = useState<{ 
    open: boolean; 
    message: string; 
    severity: 'success' | 'error' | 'warning';
  }>({
    open: false,
    message: '',
    severity: 'success'
  });

  // 더미 태그 데이터
  const dummyTags: Tag[] = [
    {
      id: 1,
      name: '확장팩',
      slug: 'expansion',
      description: '새로운 확장팩 관련 소식',
      color: '#2196f3',
      usageCount: 15,
      createdAt: '2024-01-15T10:00:00'
    },
    {
      id: 2,
      name: '드래곤',
      slug: 'dragon',
      description: '드래곤 관련 카드와 소식',
      color: '#f44336',
      usageCount: 8,
      createdAt: '2024-02-01T10:00:00'
    },
    {
      id: 3,
      name: '점검',
      slug: 'maintenance',
      description: '시스템 점검 관련',
      color: '#ff9800',
      usageCount: 12,
      createdAt: '2024-01-20T10:00:00'
    },
    {
      id: 4,
      name: '토너먼트',
      slug: 'tournament',
      description: '대회 및 토너먼트 소식',
      color: '#4caf50',
      usageCount: 23,
      createdAt: '2024-01-10T10:00:00'
    },
    {
      id: 5,
      name: '규정',
      slug: 'rules',
      description: '게임 규정 업데이트',
      color: '#9c27b0',
      usageCount: 5,
      createdAt: '2024-03-01T10:00:00'
    },
    {
      id: 6,
      name: '신규카드',
      slug: 'new-card',
      description: '새로 출시되는 카드들',
      color: '#00bcd4',
      usageCount: 0,
      createdAt: '2024-08-01T10:00:00'
    }
  ];

  useEffect(() => {
    loadTags();
  }, [page, rowsPerPage, searchKeyword]);

  const loadTags = async () => {
    setLoading(true);
    try {
      // TODO: 실제 API 호출로 대체
      // const response = await fetch(`/api/v1/admin/tags?page=${page}&size=${rowsPerPage}&search=${searchKeyword}`);
      
      // 더미 데이터 필터링
      let filteredTags = dummyTags;
      
      if (searchKeyword) {
        filteredTags = filteredTags.filter(tag => 
          tag.name.toLowerCase().includes(searchKeyword.toLowerCase()) ||
          (tag.description && tag.description.toLowerCase().includes(searchKeyword.toLowerCase()))
        );
      }
      
      setTags(filteredTags);
      setTotalCount(filteredTags.length);
      
    } catch (error) {
      console.error('Failed to load tags:', error);
      showSnackbar('태그를 불러오는 중 오류가 발생했습니다.', 'error');
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

  const handleCreateNew = () => {
    setEditDialog({
      open: true,
      tag: {
        id: 0,
        name: '',
        slug: '',
        description: '',
        color: '#2196f3',
        usageCount: 0,
        createdAt: new Date().toISOString()
      },
      isNew: true
    });
  };

  const handleEdit = (tag: Tag) => {
    setEditDialog({
      open: true,
      tag: { ...tag },
      isNew: false
    });
    setMenuAnchor({ element: null, tag: null });
  };

  const handleDelete = (tag: Tag) => {
    setDeleteDialog({ open: true, tag });
    setMenuAnchor({ element: null, tag: null });
  };

  const handleSaveTag = async (tagData: Tag) => {
    try {
      if (editDialog.isNew) {
        // TODO: 실제 API 호출
        // await fetch('/api/v1/admin/tags', { method: 'POST', body: JSON.stringify(tagData) });
        showSnackbar('태그가 생성되었습니다.', 'success');
      } else {
        // TODO: 실제 API 호출
        // await fetch(`/api/v1/admin/tags/${tagData.id}`, { method: 'PUT', body: JSON.stringify(tagData) });
        showSnackbar('태그가 수정되었습니다.', 'success');
      }
      
      setEditDialog({ open: false, tag: null, isNew: false });
      loadTags();
    } catch (error) {
      showSnackbar('태그 저장 중 오류가 발생했습니다.', 'error');
    }
  };

  const handleConfirmDelete = async () => {
    if (!deleteDialog.tag) return;
    
    try {
      // TODO: 실제 API 호출
      // await fetch(`/api/v1/admin/tags/${deleteDialog.tag.id}`, { method: 'DELETE' });
      
      showSnackbar('태그가 삭제되었습니다.', 'success');
      setDeleteDialog({ open: false, tag: null });
      loadTags();
    } catch (error) {
      showSnackbar('태그 삭제 중 오류가 발생했습니다.', 'error');
    }
  };

  const handleRecalculateUsage = async (tag: Tag) => {
    try {
      // TODO: 실제 API 호출
      // await fetch(`/api/v1/admin/tags/${tag.id}/recalculate-usage`, { method: 'POST' });
      
      showSnackbar('사용 횟수가 재계산되었습니다.', 'success');
      setMenuAnchor({ element: null, tag: null });
      loadTags();
    } catch (error) {
      showSnackbar('사용 횟수 재계산 중 오류가 발생했습니다.', 'error');
    }
  };

  const handleCleanupUnused = async () => {
    try {
      // TODO: 실제 API 호출
      // const response = await fetch('/api/v1/admin/tags/unused', { method: 'DELETE' });
      // const result = await response.json();
      
      const unusedCount = tags.filter(tag => tag.usageCount === 0).length;
      showSnackbar(`${unusedCount}개의 사용되지 않은 태그가 삭제되었습니다.`, 'success');
      loadTags();
    } catch (error) {
      showSnackbar('태그 정리 중 오류가 발생했습니다.', 'error');
    }
  };

  const showSnackbar = (message: string, severity: 'success' | 'error' | 'warning') => {
    setSnackbar({ open: true, message, severity });
  };

  const generateSlug = (name: string) => {
    return name
      .toLowerCase()
      .replace(/[^a-z0-9가-힣\s-]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .trim();
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
            태그 관리
          </Typography>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button 
              startIcon={<CleaningServices />}
              onClick={handleCleanupUnused}
              variant="outlined"
              color="warning"
            >
              미사용 태그 정리
            </Button>
            <Button 
              startIcon={<Add />}
              variant="contained"
              onClick={handleCreateNew}
              sx={{ 
                backgroundColor: '#b8191c',
                '&:hover': { backgroundColor: '#a01018' }
              }}
            >
              새 태그 생성
            </Button>
          </Box>
        </Box>

        {/* 검색 */}
        <Box sx={{ mb: 3 }}>
          <TextField 
            size="small" 
            placeholder="태그명, 설명 검색..." 
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            sx={{ minWidth: 300 }}
          />
        </Box>

        {/* 태그 테이블 */}
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>태그명</TableCell>
                <TableCell>슬러그</TableCell>
                <TableCell>설명</TableCell>
                <TableCell>색상</TableCell>
                <TableCell align="center">사용횟수</TableCell>
                <TableCell>생성일</TableCell>
                <TableCell align="center">액션</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {tags.map((tag) => (
                <TableRow key={tag.id} hover>
                  <TableCell>
                    <Chip 
                      label={tag.name}
                      size="small"
                      style={{ 
                        backgroundColor: tag.color || '#2196f3',
                        color: 'white'
                      }}
                    />
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                      {tag.slug}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" color="text.secondary">
                      {tag.description || '-'}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Box 
                      sx={{ 
                        width: 24, 
                        height: 24, 
                        backgroundColor: tag.color || '#2196f3',
                        borderRadius: '50%',
                        border: '1px solid #e0e0e0'
                      }}
                    />
                  </TableCell>
                  <TableCell align="center">
                    <Chip 
                      label={tag.usageCount}
                      size="small"
                      color={tag.usageCount === 0 ? 'default' : 'primary'}
                      variant={tag.usageCount === 0 ? 'outlined' : 'filled'}
                    />
                  </TableCell>
                  <TableCell>
                    {new Date(tag.createdAt).toLocaleDateString('ko-KR')}
                  </TableCell>
                  <TableCell align="center">
                    <IconButton 
                      size="small"
                      onClick={(e) => setMenuAnchor({ element: e.currentTarget, tag })}
                    >
                      <MoreVert />
                    </IconButton>
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

        {/* 메뉴 */}
        <Menu
          anchorEl={menuAnchor.element}
          open={Boolean(menuAnchor.element)}
          onClose={() => setMenuAnchor({ element: null, tag: null })}
        >
          <MenuItem onClick={() => menuAnchor.tag && handleEdit(menuAnchor.tag)}>
            <Edit sx={{ mr: 1 }} fontSize="small" />
            수정
          </MenuItem>
          <MenuItem onClick={() => menuAnchor.tag && handleRecalculateUsage(menuAnchor.tag)}>
            <Refresh sx={{ mr: 1 }} fontSize="small" />
            사용 횟수 재계산
          </MenuItem>
          <MenuItem 
            onClick={() => menuAnchor.tag && handleDelete(menuAnchor.tag)}
            sx={{ color: 'error.main' }}
            disabled={menuAnchor.tag?.usageCount !== 0}
          >
            <Delete sx={{ mr: 1 }} fontSize="small" />
            삭제
          </MenuItem>
        </Menu>

        {/* 태그 편집 다이얼로그 */}
        <TagEditDialog
          open={editDialog.open}
          tag={editDialog.tag}
          isNew={editDialog.isNew}
          onClose={() => setEditDialog({ open: false, tag: null, isNew: false })}
          onSave={handleSaveTag}
          generateSlug={generateSlug}
        />

        {/* 삭제 확인 다이얼로그 */}
        <Dialog
          open={deleteDialog.open}
          onClose={() => setDeleteDialog({ open: false, tag: null })}
        >
          <DialogTitle>태그 삭제</DialogTitle>
          <DialogContent>
            <Typography>
              "{deleteDialog.tag?.name}" 태그를 삭제하시겠습니까?
            </Typography>
            {deleteDialog.tag?.usageCount === 0 ? (
              <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                이 태그는 사용되지 않으므로 안전하게 삭제할 수 있습니다.
              </Typography>
            ) : (
              <Alert severity="warning" sx={{ mt: 2 }}>
                이 태그는 {deleteDialog.tag?.usageCount}개의 게시글에서 사용 중입니다.
                삭제하면 해당 게시글에서 태그가 제거됩니다.
              </Alert>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDeleteDialog({ open: false, tag: null })}>
              취소
            </Button>
            <Button onClick={handleConfirmDelete} color="error" variant="contained">
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

// 태그 편집 다이얼로그 컴포넌트
interface TagEditDialogProps {
  open: boolean;
  tag: Tag | null;
  isNew: boolean;
  onClose: () => void;
  onSave: (tag: Tag) => void;
  generateSlug: (name: string) => string;
}

const TagEditDialog: React.FC<TagEditDialogProps> = ({ 
  open, 
  tag, 
  isNew, 
  onClose, 
  onSave,
  generateSlug 
}) => {
  const [formData, setFormData] = useState<Tag>({
    id: 0,
    name: '',
    slug: '',
    description: '',
    color: '#2196f3',
    usageCount: 0,
    createdAt: new Date().toISOString()
  });

  useEffect(() => {
    if (tag) {
      setFormData(tag);
    }
  }, [tag, open]);

  const handleInputChange = (field: keyof Tag, value: any) => {
    setFormData(prev => {
      const updated = { ...prev, [field]: value };
      
      // 태그명이 변경되면 자동으로 슬러그 생성
      if (field === 'name') {
        updated.slug = generateSlug(value);
      }
      
      return updated;
    });
  };

  const handleSave = () => {
    if (!formData.name.trim()) {
      return;
    }
    onSave(formData);
  };

  const colorOptions = [
    '#2196f3', '#f44336', '#4caf50', '#ff9800', '#9c27b0',
    '#00bcd4', '#795548', '#607d8b', '#e91e63', '#3f51b5'
  ];

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {isNew ? '새 태그 생성' : '태그 수정'}
      </DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 1 }}>
          <TextField
            fullWidth
            label="태그명"
            value={formData.name}
            onChange={(e) => handleInputChange('name', e.target.value)}
            margin="normal"
            required
          />
          
          <TextField
            fullWidth
            label="슬러그"
            value={formData.slug}
            onChange={(e) => handleInputChange('slug', e.target.value)}
            margin="normal"
            helperText="URL에 사용될 태그 식별자입니다. 영문, 숫자, 하이픈만 사용 가능합니다."
            sx={{ '& input': { fontFamily: 'monospace' } }}
          />
          
          <TextField
            fullWidth
            label="설명"
            value={formData.description}
            onChange={(e) => handleInputChange('description', e.target.value)}
            margin="normal"
            multiline
            rows={2}
            helperText="태그에 대한 간단한 설명을 입력하세요."
          />
          
          <Box sx={{ mt: 2, mb: 1 }}>
            <Typography variant="body2" sx={{ mb: 1 }}>색상</Typography>
            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
              {colorOptions.map((color) => (
                <Box
                  key={color}
                  onClick={() => handleInputChange('color', color)}
                  sx={{
                    width: 32,
                    height: 32,
                    backgroundColor: color,
                    borderRadius: '50%',
                    cursor: 'pointer',
                    border: formData.color === color ? '3px solid #000' : '1px solid #e0e0e0',
                    '&:hover': {
                      transform: 'scale(1.1)'
                    }
                  }}
                />
              ))}
            </Box>
          </Box>
          
          {!isNew && (
            <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
              사용 횟수: {formData.usageCount}회
            </Typography>
          )}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>취소</Button>
        <Button 
          onClick={handleSave} 
          variant="contained"
          disabled={!formData.name.trim()}
        >
          {isNew ? '생성' : '수정'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TagManagement;