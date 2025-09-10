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
  FormControlLabel,
  InputLabel,
  MenuItem,
  Select,
  Switch,
  TextField,
  Typography,
  Autocomplete,
  Alert,
  Snackbar
} from '@mui/material';
import {
  Save,
  Preview,
  CloudUpload,
  Add
} from '@mui/icons-material';
import { articleApi, tagApi, Article as ApiArticle, Tag as ApiTag } from '../../services/api';

interface ArticleFormData {
  id?: number;
  title: string;
  content: string;
  summary?: string;
  author: string;
  authorId?: number;
  status: 'PUBLIC' | 'PRIVATE';
  category: 'NEWS' | 'ANNOUNCEMENT' | 'PRODUCT_INFO' | 'UPDATE' | 'EVENT';
  featured?: boolean;
  thumbnailUrl?: string;
  metaTitle?: string;
  metaDescription?: string;
  publishDate?: string;
  tags?: ApiTag[];
}

interface ArticleEditorProps {
  open: boolean;
  article?: ApiArticle | null;
  onClose: () => void;
  onSave?: (article: ApiArticle) => void;
}

const ArticleEditor: React.FC<ArticleEditorProps> = ({ open, article, onClose, onSave }) => {
  // 현재 로그인한 사용자 정보 가져오기
  const getCurrentUser = () => {
    const userStr = localStorage.getItem('tcg_lounge_user');
    if (userStr) {
      try {
        return JSON.parse(userStr);
      } catch (e) {
        console.error('Failed to parse user data:', e);
        return null;
      }
    }
    return null;
  };

  const currentUser = getCurrentUser();
  const [formData, setFormData] = useState<ArticleFormData>({
    title: '',
    content: '',
    summary: '',
    author: '관리자',
    authorId: 1,
    status: 'PRIVATE',
    category: 'NEWS',
    featured: false,
    thumbnailUrl: '',
    metaTitle: '',
    metaDescription: '',
    tags: []
  });
  
  const [availableTags, setAvailableTags] = useState<ApiTag[]>([]);
  const [selectedTags, setSelectedTags] = useState<ApiTag[]>([]);
  const [newTagName, setNewTagName] = useState('');
  const [previewMode, setPreviewMode] = useState(false);
  const [saving, setSaving] = useState(false);
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: 'success' | 'error' }>({
    open: false,
    message: '',
    severity: 'success'
  });

  useEffect(() => {
    loadTags();
  }, []);

  const loadTags = async () => {
    try {
      const tags = await tagApi.getTags();
      setAvailableTags(tags);
    } catch (error) {
      console.error('Failed to load tags:', error);
      showSnackbar('태그를 불러오는 중 오류가 발생했습니다.', 'error');
    }
  };

  useEffect(() => {
    if (article) {
      setFormData({
        title: article.title || '',
        content: article.content || '',
        summary: article.summary || '',
        author: article.author || currentUser?.username || '관리자',
        authorId: article.authorId || currentUser?.uid || 102,
        status: article.status || 'PRIVATE',
        category: article.category || 'NEWS',
        featured: article.featured || false,
        thumbnailUrl: article.thumbnailUrl || '',
        metaTitle: article.metaTitle || '',
        metaDescription: article.metaDescription || '',
        tags: article.tags || [],
        publishDate: article.publishDate ? article.publishDate.split('T')[0] : ''
      });
      setSelectedTags(article.tags || []);
    } else {
      // 새 게시글
      setFormData({
        title: '',
        content: '',
        summary: '',
        author: currentUser?.username || '관리자',
        authorId: currentUser?.uid || 102,
        status: 'PRIVATE',
        category: 'NEWS',
        featured: false,
        thumbnailUrl: '',
        metaTitle: '',
        metaDescription: '',
        tags: []
      });
      setSelectedTags([]);
    }
    setPreviewMode(false);
  }, [article, open]);

  const handleInputChange = (field: keyof ArticleFormData, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleAddTag = async () => {
    if (newTagName.trim()) {
      try {
        const newTag = await tagApi.createTag({
          name: newTagName.trim(),
          slug: newTagName.toLowerCase().replace(/\s+/g, '-')
        });
        
        if (!selectedTags.some(tag => tag.name === newTag.name)) {
          setSelectedTags(prev => [...prev, newTag]);
          setAvailableTags(prev => [...prev, newTag]);
        }
        
        setNewTagName('');
        showSnackbar('새 태그가 생성되었습니다.', 'success');
      } catch (error) {
        console.error('Failed to create tag:', error);
        showSnackbar('태그 생성 중 오류가 발생했습니다.', 'error');
      }
    }
  };

  const handleSave = async (publishNow = false) => {
    if (!formData.title.trim() || !formData.content.trim()) {
      showSnackbar('제목과 내용을 입력해주세요.', 'error');
      return;
    }

    setSaving(true);
    try {
      const articleData = {
        title: formData.title,
        content: formData.content,
        summary: formData.summary,
        author: formData.author,
        authorId: formData.authorId,
        status: publishNow ? 'PUBLIC' : formData.status,
        category: formData.category,
        featured: formData.featured,
        thumbnailUrl: formData.thumbnailUrl,
        metaTitle: formData.metaTitle,
        metaDescription: formData.metaDescription,
        tagNames: selectedTags.map(tag => tag.name)
      };

      if (article?.id) {
        // 수정
        const updatedArticle = await articleApi.updateArticle(article.id, articleData);
        showSnackbar('게시글이 수정되었습니다.', 'success');
        onSave?.(updatedArticle);
      } else {
        // 새 게시글
        const newArticle = await articleApi.createArticle(articleData);
        showSnackbar('게시글이 저장되었습니다.', 'success');
        onSave?.(newArticle);
      }
      setTimeout(() => onClose(), 1000);
    } catch (error) {
      showSnackbar('저장 중 오류가 발생했습니다.', 'error');
    } finally {
      setSaving(false);
    }
  };

  const showSnackbar = (message: string, severity: 'success' | 'error') => {
    setSnackbar({ open: true, message, severity });
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

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: { height: '90vh' }
      }}
    >
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h6">
          {article?.id ? '게시글 수정' : '새 게시글 작성'}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button 
            startIcon={<Preview />}
            onClick={() => setPreviewMode(!previewMode)}
            variant={previewMode ? 'contained' : 'outlined'}
          >
            미리보기
          </Button>
        </Box>
      </DialogTitle>

      <DialogContent dividers sx={{ p: 0 }}>
        <Box sx={{ height: '100%', display: 'flex' }}>
          {/* 편집 영역 */}
          <Box sx={{ 
            flex: previewMode ? 1 : 2, 
            p: 3, 
            borderRight: previewMode ? '1px solid #e0e0e0' : 'none',
            overflow: 'auto'
          }}>
            <Box>
              {/* 기본 정보 */}
              <Box sx={{ mb: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>기본 정보</Typography>
                
                <TextField
                  fullWidth
                  label="제목"
                  value={formData.title}
                  onChange={(e) => handleInputChange('title', e.target.value)}
                  margin="normal"
                  required
                />
                
                <TextField
                  fullWidth
                  label="요약"
                  value={formData.summary}
                  onChange={(e) => handleInputChange('summary', e.target.value)}
                  margin="normal"
                  multiline
                  rows={2}
                  helperText="검색 결과나 목록에서 표시될 간단한 요약입니다."
                />
                
                <TextField
                  fullWidth
                  label="내용"
                  value={formData.content}
                  onChange={(e) => handleInputChange('content', e.target.value)}
                  margin="normal"
                  multiline
                  rows={10}
                  required
                />
              </Box>

              {/* 카테고리 및 상태 */}
              <Box sx={{ flex: 1 }}>
                <FormControl fullWidth margin="normal">
                  <InputLabel>카테고리</InputLabel>
                  <Select 
                    value={formData.category}
                    onChange={(e) => handleInputChange('category', e.target.value)}
                    label="카테고리"
                  >
                    <MenuItem value="NEWS">뉴스</MenuItem>
                    <MenuItem value="ANNOUNCEMENT">공지사항</MenuItem>
                    <MenuItem value="PRODUCT_INFO">제품정보</MenuItem>
                    <MenuItem value="UPDATE">업데이트</MenuItem>
                    <MenuItem value="EVENT">이벤트</MenuItem>
                  </Select>
                </FormControl>
              </Box>

              <Box sx={{ flex: 1 }}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.status === 'PUBLIC'}
                      onChange={(e) => handleInputChange('status', e.target.checked ? 'PUBLIC' : 'PRIVATE')}
                      color="primary"
                    />
                  }
                  label={formData.status === 'PUBLIC' ? '공개' : '비공개'}
                  sx={{ mt: 2 }}
                />
              </Box>

              {/* 태그 관리 */}
              <Box sx={{ mb: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>태그</Typography>
                
                <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                  {selectedTags.map((tag) => (
                    <Chip
                      key={tag.id}
                      label={tag.name}
                      onDelete={() => setSelectedTags(prev => prev.filter(t => t.id !== tag.id))}
                      color="primary"
                      variant="outlined"
                    />
                  ))}
                </Box>

                <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
                  <Autocomplete
                    options={availableTags.filter(tag => !selectedTags.some(selected => selected.id === tag.id))}
                    getOptionLabel={(option) => option.name}
                    onChange={(event, value) => {
                      if (value) {
                        setSelectedTags(prev => [...prev, value]);
                      }
                    }}
                    renderInput={(params) => (
                      <TextField {...params} label="기존 태그 선택" size="small" sx={{ minWidth: 200 }} />
                    )}
                    sx={{ flex: 1 }}
                  />
                  
                  <TextField
                    label="새 태그 추가"
                    value={newTagName}
                    onChange={(e) => setNewTagName(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleAddTag()}
                    size="small"
                    sx={{ minWidth: 150 }}
                  />
                  
                  <Button
                    onClick={handleAddTag}
                    variant="outlined"
                    startIcon={<Add />}
                    disabled={!newTagName.trim()}
                  >
                    추가
                  </Button>
                </Box>
              </Box>

              {/* 추가 설정 */}
              <Box sx={{ mb: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>추가 설정</Typography>
                
                <FormControlLabel
                  control={
                    <Switch 
                      checked={formData.featured || false}
                      onChange={(e) => handleInputChange('featured', e.target.checked)}
                    />
                  }
                  label="주요 뉴스로 설정"
                />

                <TextField
                  fullWidth
                  label="썸네일 URL"
                  value={formData.thumbnailUrl}
                  onChange={(e) => handleInputChange('thumbnailUrl', e.target.value)}
                  margin="normal"
                  placeholder="https://example.com/image.jpg"
                />

                <TextField
                  fullWidth
                  label="게시 예정일"
                  type="date"
                  value={formData.publishDate || ''}
                  onChange={(e) => handleInputChange('publishDate', e.target.value)}
                  margin="normal"
                  InputLabelProps={{
                    shrink: true,
                  }}
                  helperText="설정하지 않으면 즉시 게시됩니다."
                />
              </Box>

              {/* SEO 설정 */}
              <Box sx={{ mb: 3 }}>
                <Typography variant="h6" sx={{ mb: 2 }}>SEO 설정</Typography>
                
                <TextField
                  fullWidth
                  label="메타 제목"
                  value={formData.metaTitle}
                  onChange={(e) => handleInputChange('metaTitle', e.target.value)}
                  margin="normal"
                  helperText="검색 엔진에 표시될 제목 (60자 이하 권장)"
                />

                <TextField
                  fullWidth
                  label="메타 설명"
                  value={formData.metaDescription}
                  onChange={(e) => handleInputChange('metaDescription', e.target.value)}
                  margin="normal"
                  multiline
                  rows={3}
                  helperText="검색 엔진에 표시될 설명 (160자 이하 권장)"
                />
              </Box>
            </Box>
          </Box>

          {/* 미리보기 영역 */}
          {previewMode && (
            <Box sx={{ flex: 1, p: 3, bgcolor: '#fafafa', overflow: 'auto' }}>
              <Typography variant="h6" sx={{ mb: 2 }}>미리보기</Typography>
              
              <Card>
                <CardContent>
                  <Box sx={{ mb: 2 }}>
                    <Chip 
                      label={getCategoryLabel(formData.category)}
                      size="small" 
                      color="primary" 
                    />
                    {formData.featured && (
                      <Chip 
                        label="주요뉴스" 
                        size="small" 
                        color="warning" 
                        sx={{ ml: 1 }}
                      />
                    )}
                  </Box>
                  
                  <Typography variant="h4" sx={{ mb: 2, fontWeight: 'bold' }}>
                    {formData.title || '제목을 입력하세요'}
                  </Typography>
                  
                  {formData.summary && (
                    <Typography variant="body1" color="text.secondary" sx={{ mb: 2, fontStyle: 'italic' }}>
                      {formData.summary}
                    </Typography>
                  )}
                  
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    작성자: {formData.author} | 작성일: {new Date().toLocaleDateString('ko-KR')}
                  </Typography>
                  
                  <Box sx={{ mb: 2 }}>
                    {selectedTags.map((tag) => (
                      <Chip
                        key={tag.id}
                        label={tag.name}
                        size="small"
                        variant="outlined"
                        sx={{ mr: 0.5, mb: 0.5 }}
                      />
                    ))}
                  </Box>
                  
                  {formData.thumbnailUrl && (
                    <Box sx={{ mb: 2 }}>
                      <img 
                        src={formData.thumbnailUrl} 
                        alt="썸네일"
                        style={{ 
                          width: '100%', 
                          maxHeight: '200px', 
                          objectFit: 'cover',
                          borderRadius: '4px'
                        }}
                        onError={(e) => {
                          (e.target as HTMLImageElement).style.display = 'none';
                        }}
                      />
                    </Box>
                  )}
                  
                  <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.8 }}>
                    {formData.content || '내용을 입력하세요'}
                  </Typography>
                </CardContent>
              </Card>
            </Box>
          )}
        </Box>
      </DialogContent>

      <DialogActions sx={{ p: 2, gap: 1 }}>
        <Button onClick={onClose} disabled={saving}>
          취소
        </Button>
        <Button 
          onClick={() => handleSave(false)} 
          disabled={saving}
          startIcon={<Save />}
        >
          임시저장
        </Button>
        <Button 
          onClick={() => handleSave(true)} 
          disabled={saving}
          variant="contained"
          startIcon={<CloudUpload />}
          sx={{ 
            backgroundColor: '#b8191c',
            '&:hover': { backgroundColor: '#a01018' }
          }}
        >
          게시하기
        </Button>
      </DialogActions>

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
    </Dialog>
  );
};

export default ArticleEditor;