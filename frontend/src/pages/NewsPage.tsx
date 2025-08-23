import React, { useState } from 'react';
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

const NewsPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  // 샘플 뉴스 데이터
  const newsData = [
    {
      id: 1,
      title: '신규 확장팩 "드래곤의 귀환" 8월 30일 출시!',
      content: '오랜 기다림 끝에 드디어 새로운 확장팩이 출시됩니다. 강력한 드래곤 카드들과 새로운 메커니즘을 만나보세요.',
      category: 'PRODUCT',
      date: '2024-08-20',
      author: 'ESTELA 개발팀',
      views: 2847,
      image: 'https://via.placeholder.com/400x250/667eea/ffffff?text=드래곤의+귀환',
      featured: true
    },
    {
      id: 2,
      title: '월드 챔피언십 2024 참가자 모집',
      content: '전 세계 최고의 플레이어들이 모이는 월드 챔피언십이 10월에 개최됩니다. 지금 바로 예선에 참가해보세요!',
      category: 'TOURNAMENT',
      date: '2024-08-19',
      author: '대회 운영팀',
      views: 1923,
      image: 'https://via.placeholder.com/400x250/f093fb/ffffff?text=월드+챔피언십',
      featured: true
    },
    {
      id: 3,
      title: '8월 밸런스 패치 노트',
      content: '일부 카드의 밸런스 조정이 이루어졌습니다. 자세한 변경사항을 확인해보세요.',
      category: 'UPDATE',
      date: '2024-08-18',
      author: 'ESTELA 개발팀',
      views: 3456,
      image: 'https://via.placeholder.com/400x250/4facfe/ffffff?text=밸런스+패치',
      featured: false
    },
    {
      id: 4,
      title: '여름 특별 이벤트 - 카드팩 30% 할인',
      content: '8월 한 달 동안 모든 카드팩이 30% 할인됩니다. 이 기회를 놓치지 마세요!',
      category: 'EVENT',
      date: '2024-08-17',
      author: '마케팅팀',
      views: 5678,
      image: 'https://via.placeholder.com/400x250/fa709a/ffffff?text=여름+할인',
      featured: false
    },
    {
      id: 5,
      title: '초보자를 위한 덱 빌딩 가이드',
      content: 'TCG를 처음 시작하는 분들을 위한 덱 구성 팁과 전략을 소개합니다.',
      category: 'GUIDE',
      date: '2024-08-16',
      author: '커뮤니티팀',
      views: 1234,
      image: 'https://via.placeholder.com/400x250/84fab0/ffffff?text=초보자+가이드',
      featured: false
    },
    {
      id: 6,
      title: '프로 플레이어 인터뷰 - 김프로',
      content: '최근 대회에서 우승한 김프로 선수와의 인터뷰를 공개합니다.',
      category: 'INTERVIEW',
      date: '2024-08-15',
      author: '미디어팀',
      views: 2890,
      image: 'https://via.placeholder.com/400x250/764ba2/ffffff?text=프로+인터뷰',
      featured: false
    }
  ];

  const categories = [
    { key: 'ALL', label: '전체', icon: <TrendingUp /> },
    { key: 'PRODUCT', label: '제품', icon: <NewReleases /> },
    { key: 'TOURNAMENT', label: '대회', icon: <Event /> },
    { key: 'UPDATE', label: '업데이트', icon: <Announcement /> }
  ];

  const getCategoryLabel = (category: string) => {
    switch (category) {
      case 'PRODUCT': return '제품';
      case 'TOURNAMENT': return '대회';
      case 'UPDATE': return '업데이트';
      case 'EVENT': return '이벤트';
      case 'GUIDE': return '가이드';
      case 'INTERVIEW': return '인터뷰';
      default: return '기타';
    }
  };

  const getCategoryColor = (category: string) => {
    switch (category) {
      case 'PRODUCT': return '#4caf50';
      case 'TOURNAMENT': return '#f44336';
      case 'UPDATE': return '#2196f3';
      case 'EVENT': return '#ff9800';
      case 'GUIDE': return '#9c27b0';
      case 'INTERVIEW': return '#795548';
      default: return '#757575';
    }
  };

  const filteredNews = newsData.filter(news => {
    const matchesSearch = news.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         news.content.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = tabValue === 0 || 
                           (tabValue === 1 && news.category === 'PRODUCT') ||
                           (tabValue === 2 && news.category === 'TOURNAMENT') ||
                           (tabValue === 3 && news.category === 'UPDATE');
    return matchesSearch && matchesCategory;
  });

  const featuredNews = newsData.filter(news => news.featured);

  return (
    <Box>
      <Navigation />

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
        {tabValue === 0 && featuredNews.length > 0 && (
          <Box sx={{ mb: 6 }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
              주요 소식
            </Typography>
            <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
              {featuredNews.map((news) => (
                <Box key={news.id} sx={{ flex: '1 1 400px', minWidth: '400px' }}>
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
                      image={news.image}
                      alt={news.title}
                    />
                    <CardContent>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <Chip 
                          label={getCategoryLabel(news.category)}
                          size="small"
                          sx={{ 
                            backgroundColor: getCategoryColor(news.category),
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
                        {news.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                        {news.content}
                      </Typography>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="caption" color="text.secondary">
                          {news.date} | {news.author}
                        </Typography>
                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                          <Visibility sx={{ fontSize: 16, mr: 0.5, color: 'text.secondary' }} />
                          <Typography variant="caption" color="text.secondary">
                            {news.views.toLocaleString()}
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
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {filteredNews.map((news) => (
              <Box key={news.id} sx={{ flex: '1 1 350px', minWidth: '350px' }}>
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
                    image={news.image}
                    alt={news.title}
                  />
                  <CardContent>
                    <Chip 
                      label={getCategoryLabel(news.category)}
                      size="small"
                      sx={{ 
                        backgroundColor: getCategoryColor(news.category),
                        color: 'white',
                        mb: 1
                      }}
                    />
                    <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1, fontSize: '1rem' }}>
                      {news.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2, height: '40px', overflow: 'hidden' }}>
                      {news.content}
                    </Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Typography variant="caption" color="text.secondary">
                        {news.date}
                      </Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Visibility sx={{ fontSize: 14, mr: 0.5, color: 'text.secondary' }} />
                        <Typography variant="caption" color="text.secondary">
                          {news.views.toLocaleString()}
                        </Typography>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        </Box>

        {/* 페이지네이션 */}
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Pagination 
            count={Math.ceil(filteredNews.length / 6)} 
            page={currentPage}
            onChange={(event, value) => setCurrentPage(value)}
            color="primary"
            size="large"
          />
        </Box>
      </Container>
    </Box>
  );
};

export default NewsPage;