import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  CardMedia,
  Grid,
  Chip,
  TextField,
  InputAdornment,
  Pagination,
  Tabs,
  Tab,
  IconButton
} from '@mui/material';
import {
  Search,
  CalendarToday,
  Visibility,
  TrendingUp,
  Announcement,
  NewReleases,
  Event
} from '@mui/icons-material';
import Navigation from '../components/Navigation';
import LoginModal from '../components/LoginModal';
import { articleApi, Article } from '../services/api';

const NewsPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [articles, setArticles] = useState<Article[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(1);
  const [loginModalOpen, setLoginModalOpen] = useState(false);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
    setCurrentPage(1); // Reset page when changing tabs
  };

  // 검색 디바운스를 위한 타이머
  useEffect(() => {
    const timer = setTimeout(() => {
      loadArticles();
    }, searchQuery ? 500 : 0); // 검색어가 있을 때만 딜레이 적용

    return () => clearTimeout(timer);
  }, [tabValue, searchQuery, currentPage]);

  const loadArticles = async () => {
    setLoading(true);
    try {
      const params = {
        page: currentPage - 1, // Backend uses 0-based pagination
        size: 6,
        ...(searchQuery && { search: searchQuery }),
        ...(getCategoryFilter() && { category: getCategoryFilter() })
      };

      const response = await articleApi.getArticles(params);
      setArticles(response.content);
      setTotalPages(response.totalPages);
    } catch (error) {
      console.error('Failed to load articles:', error);
      setArticles([]);
    } finally {
      setLoading(false);
    }
  };

  const getCategoryFilter = () => {
    switch (tabValue) {
      case 1: return 'PRODUCT_INFO';
      case 2: return 'EVENT';
      case 3: return 'UPDATE';
      default: return undefined; // All categories
    }
  };

  const categories = [
    { key: 'ALL', label: '전체', icon: <TrendingUp /> },
    { key: 'PRODUCT_INFO', label: '제품정보', icon: <NewReleases /> },
    { key: 'EVENT', label: '이벤트', icon: <Event /> },
    { key: 'UPDATE', label: '업데이트', icon: <Announcement /> }
  ];

  const getCategoryLabel = (category: string) => {
    switch (category) {
      case 'NEWS': return '뉴스';
      case 'ANNOUNCEMENT': return '공지사항';
      case 'PRODUCT_INFO': return '제품정보';
      case 'UPDATE': return '업데이트';
      case 'EVENT': return '이벤트';
      default: return '기타';
    }
  };

  const getCategoryColor = (category: string) => {
    switch (category) {
      case 'NEWS': return '#2196f3';
      case 'ANNOUNCEMENT': return '#f44336';
      case 'PRODUCT_INFO': return '#4caf50';
      case 'UPDATE': return '#2196f3';
      case 'EVENT': return '#ff9800';
      default: return '#757575';
    }
  };

  // Featured articles (실제로는 API에서 featured=true로 필터링)
  const featuredNews = articles.filter(article => article.featured);

  return (
    <Box>
      <Navigation onLoginClick={() => setLoginModalOpen(true)} />

      {/* 메인 콘텐츠 */}
      <Container maxWidth="lg" sx={{ pt: 12, pb: 4 }}>
        {/* 페이지 헤더 */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2, color: '#333' }}>
            새소식
          </Typography>
          <Typography variant="body1" color="text.secondary">
            ESTELA TCG의 최신 소식과 업데이트를 확인하세요
          </Typography>
        </Box>

        {/* 검색 및 필터 */}
        <Box sx={{ mb: 4 }}>
          <TextField
            fullWidth
            placeholder="뉴스 검색..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search />
                </InputAdornment>
              ),
            }}
            sx={{ mb: 2 }}
          />
          
          <Tabs value={tabValue} onChange={handleTabChange} sx={{ borderBottom: 1, borderColor: 'divider' }}>
            {categories.map((category, index) => (
              <Tab 
                key={category.key}
                icon={category.icon} 
                label={category.label} 
                iconPosition="start"
              />
            ))}
          </Tabs>
        </Box>

        {/* 주요 뉴스 섹션 */}
        {tabValue === 0 && !loading && featuredNews.length > 0 && (
          <Box sx={{ mb: 6 }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
              주요 소식
            </Typography>
            <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
              {featuredNews.map((article) => (
                <Box key={article.id} sx={{ flex: '1 1 400px', minWidth: '400px' }}>
                  <Card sx={{ 
                    height: '100%',
                    cursor: 'pointer',
                    '&:hover': { 
                      transform: 'translateY(-5px)',
                      boxShadow: '0 8px 25px rgba(0,0,0,0.15)'
                    },
                    transition: 'all 0.3s'
                  }}>
                    <CardMedia
                      component="img"
                      height="200"
                      image={article.thumbnailUrl || 'https://via.placeholder.com/400x250/667eea/ffffff?text=주요소식'}
                      alt={article.title}
                    />
                    <CardContent>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <Chip 
                          label={getCategoryLabel(article.category)}
                          size="small"
                          sx={{ 
                            backgroundColor: getCategoryColor(article.category),
                            color: 'white',
                            mr: 1
                          }}
                        />
                        <Chip 
                          label="주요"
                          size="small"
                          color="error"
                        />
                      </Box>
                      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                        {article.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                        {article.summary || article.content?.substring(0, 120) + '...'}
                      </Typography>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="caption" color="text.secondary">
                          {article.createdAt ? new Date(article.createdAt).toLocaleDateString('ko-KR') : ''} | {article.author}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <Visibility sx={{ fontSize: 16, mr: 0.5, color: 'text.secondary' }} />
                          <Typography variant="caption" color="text.secondary">
                            {article.viewsCount?.toLocaleString() || '0'}
                          </Typography>
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                </Box>
              ))}
            </Box>
          </Box>
        )}

        {/* 뉴스 목록 */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
            {tabValue === 0 ? '전체 소식' : categories[tabValue].label}
          </Typography>
          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', width: '100%', py: 4 }}>
              <Typography>로딩 중...</Typography>
            </Box>
          ) : articles.length > 0 ? (
            <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
              {articles.map((article) => (
                <Box key={article.id} sx={{ flex: '1 1 350px', minWidth: '350px' }}>
                  <Card sx={{ 
                    height: '100%',
                    cursor: 'pointer',
                    '&:hover': { 
                      transform: 'translateY(-3px)',
                      boxShadow: '0 6px 20px rgba(0,0,0,0.1)'
                    },
                    transition: 'all 0.3s'
                  }}>
                    <CardMedia
                      component="img"
                      height="160"
                      image={article.thumbnailUrl || 'https://via.placeholder.com/400x250/667eea/ffffff?text=새소식'}
                      alt={article.title}
                    />
                    <CardContent>
                      <Chip 
                        label={getCategoryLabel(article.category)}
                        size="small"
                        sx={{ 
                          backgroundColor: getCategoryColor(article.category),
                          color: 'white',
                          mb: 1
                        }}
                      />
                      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1, fontSize: '1rem' }}>
                        {article.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 2, height: '40px', overflow: 'hidden' }}>
                        {article.summary || article.content?.substring(0, 80) + '...'}
                      </Typography>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="caption" color="text.secondary">
                          {article.createdAt ? new Date(article.createdAt).toLocaleDateString('ko-KR') : ''}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <Visibility sx={{ fontSize: 14, mr: 0.5, color: 'text.secondary' }} />
                          <Typography variant="caption" color="text.secondary">
                            {article.viewsCount?.toLocaleString() || '0'}
                          </Typography>
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                </Box>
              ))}
            </Box>
          ) : (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Typography color="text.secondary">표시할 게시글이 없습니다.</Typography>
            </Box>
          )}
        </Box>

        {/* 페이지네이션 */}
        {!loading && totalPages > 1 && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
            <Pagination 
              count={totalPages} 
              page={currentPage}
              onChange={(event, value) => setCurrentPage(value)}
              color="primary"
              size="large"
            />
          </Box>
        )}
      </Container>

      {/* 로그인 모달 */}
      <LoginModal 
        open={loginModalOpen} 
        onClose={() => setLoginModalOpen(false)} 
      />
    </Box>
  );
};

export default NewsPage;