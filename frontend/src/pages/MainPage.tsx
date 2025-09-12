import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Button,
  TextField,
  IconButton,
  Card,
  CardContent,
  CardMedia,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Divider,
  CircularProgress,
  Alert
} from '@mui/material';
import { 
  KeyboardArrowLeft,
  KeyboardArrowRight,
  MenuBook,
  YouTube,
  Event,
  EmojiEvents,
  Twitter,
  Instagram,
  Facebook,
  Google,
  Apple
} from '@mui/icons-material';
import Navigation from '../components/Navigation';
import LoginModal from '../components/LoginModal';
import { authAPI } from '../services/api';

const MainPage: React.FC = () => {
  const navigate = useNavigate();
  const [currentNewsSlide, setCurrentNewsSlide] = useState(0);
  const [loginModalOpen, setLoginModalOpen] = useState(false);

  // 샘플 데이터
  const newsSlides = [
    { id: 1, image: 'https://via.placeholder.com/800x400/667eea/ffffff?text=뉴스1', title: '새로운 카드팩 출시!' },
    { id: 2, image: 'https://via.placeholder.com/800x400/764ba2/ffffff?text=뉴스2', title: '대회 일정 공지' },
    { id: 3, image: 'https://via.placeholder.com/800x400/f093fb/ffffff?text=뉴스3', title: '시즌 업데이트' },
    { id: 4, image: 'https://via.placeholder.com/800x400/4facfe/ffffff?text=뉴스4', title: '이벤트 안내' },
  ];


  const newsItems = [
    { id: 1, image: 'https://via.placeholder.com/300x200/667eea/ffffff?text=소식1', title: '카드팩 프리오더', date: '2024-08-20' },
    { id: 2, image: 'https://via.placeholder.com/300x200/764ba2/ffffff?text=소식2', title: '월드 챔피언십', date: '2024-08-19' },
    { id: 3, image: 'https://via.placeholder.com/300x200/f093fb/ffffff?text=소식3', title: '밸런스 패치', date: '2024-08-18' },
    { id: 4, image: 'https://via.placeholder.com/300x200/4facfe/ffffff?text=소식4', title: '신규 확장팩', date: '2024-08-17' },
    { id: 5, image: 'https://via.placeholder.com/300x200/ff6b6b/ffffff?text=소식5', title: '커뮤니티 이벤트', date: '2024-08-16' },
    { id: 6, image: 'https://via.placeholder.com/300x200/4ecdc4/ffffff?text=소식6', title: '개발자 노트', date: '2024-08-15' },
  ];

  const handleNewsSlide = (direction: 'prev' | 'next') => {
    if (direction === 'prev') {
      setCurrentNewsSlide(prev => prev === 0 ? newsSlides.length - 1 : prev - 1);
    } else {
      setCurrentNewsSlide(prev => prev === newsSlides.length - 1 ? 0 : prev + 1);
    }
  };



  return (
    <Box>
      {/* 공통 네비게이션 바 */}
      <Navigation onLoginClick={() => setLoginModalOpen(true)} />

      {/* 컨텐츠 영역 - 네비게이션 바 높이만큼 여백 */}
      <Box sx={{ mt: 13 }}>
        
        {/* 새소식 이미지 슬라이드 */}
        <Box sx={{ backgroundColor: '#333333', py: 6 }}>
        <Container maxWidth="lg">
          <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', mb: 4, color: '#ffffff' }}>
            새소식
          </Typography>
          
          <Box sx={{ position: 'relative', display: 'flex', alignItems: 'center', gap: 2 }}>
            {/* 이전 버튼 */}
            <IconButton 
              onClick={() => handleNewsSlide('prev')}
              sx={{ 
                backgroundColor: 'white', 
                border: '2px solid #ddd',
                '&:hover': { backgroundColor: '#f5f5f5' }
              }}
            >
              <KeyboardArrowLeft />
            </IconButton>

            {/* 슬라이드 컨테이너 */}
            <Box sx={{ flex: 1, display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 2 }}>
              {/* 이전 이미지 */}
              <Box sx={{ opacity: 0.5, transform: 'scale(0.8)' }}>
                <img 
                  src={newsSlides[(currentNewsSlide - 1 + newsSlides.length) % newsSlides.length].image}
                  alt="이전"
                  style={{ width: '300px', height: '180px', objectFit: 'cover', borderRadius: '8px' }}
                />
              </Box>

              {/* 현재 이미지 */}
              <Box>
                <img 
                  src={newsSlides[currentNewsSlide].image}
                  alt={newsSlides[currentNewsSlide].title}
                  style={{ width: '500px', height: '300px', objectFit: 'cover', borderRadius: '8px' }}
                />
                <Typography variant="h6" sx={{ mt: 2, textAlign: 'center', fontWeight: 'bold', color: '#ffffff' }}>
                  {newsSlides[currentNewsSlide].title}
                </Typography>
              </Box>

              {/* 다음 이미지 */}
              <Box sx={{ opacity: 0.5, transform: 'scale(0.8)' }}>
                <img 
                  src={newsSlides[(currentNewsSlide + 1) % newsSlides.length].image}
                  alt="다음"
                  style={{ width: '300px', height: '180px', objectFit: 'cover', borderRadius: '8px' }}
                />
              </Box>
            </Box>

            {/* 다음 버튼 */}
            <IconButton 
              onClick={() => handleNewsSlide('next')}
              sx={{ 
                backgroundColor: 'white', 
                border: '2px solid #ddd',
                '&:hover': { backgroundColor: '#f5f5f5' }
              }}
            >
              <KeyboardArrowRight />
            </IconButton>
          </Box>
        </Container>
        </Box>


        {/* 기능탭 섹션 */}
        <Box sx={{ backgroundColor: '#ffffff', py: 6 }}>
        <Container maxWidth="lg">
          <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', mb: 4, color: '#333333' }}>
            주요 기능
          </Typography>
          
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
            <Card sx={{ flex: '1 1 300px', cursor: 'pointer', '&:hover': { transform: 'translateY(-5px)' }, transition: 'all 0.3s', backgroundColor: '#ffffff', border: '1px solid #ddd', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <CardContent sx={{ p: 4 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <MenuBook sx={{ mr: 2, fontSize: 32, color: '#b8191c' }} />
                  <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                    놀이방법
                  </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  TCG의 기본 룰부터 고급 전략까지 상세한 가이드를 제공합니다.
                </Typography>
                <Button variant="outlined" fullWidth sx={{ borderColor: '#ddd', color: '#666', '&:hover': { backgroundColor: '#b8191c', color: '#ffffff', borderColor: '#b8191c' } }}>
                  자세히 보기
                </Button>
              </CardContent>
            </Card>

            <Card sx={{ flex: '1 1 300px', cursor: 'pointer', '&:hover': { transform: 'translateY(-5px)' }, transition: 'all 0.3s', backgroundColor: '#ffffff', border: '1px solid #ddd', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <CardContent sx={{ p: 4 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <YouTube sx={{ mr: 2, fontSize: 32, color: '#b8191c' }} />
                  <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                    TCG 유튜브
                  </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  공식 유튜브 채널에서 최신 카드 리뷰와 덱 가이드를 확인하세요.
                </Typography>
                <Button variant="outlined" fullWidth sx={{ borderColor: '#ddd', color: '#666', '&:hover': { backgroundColor: '#b8191c', color: '#ffffff', borderColor: '#b8191c' } }}>
                  채널 보기
                </Button>
              </CardContent>
            </Card>

            <Card sx={{ flex: '1 1 300px', cursor: 'pointer', '&:hover': { transform: 'translateY(-5px)' }, transition: 'all 0.3s', backgroundColor: '#ffffff', border: '1px solid #ddd', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <CardContent sx={{ p: 4 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Event sx={{ mr: 2, fontSize: 32, color: '#b8191c' }} />
                  <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                    이벤트 참여
                  </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  다양한 이벤트에 참여하고 특별 보상을 획득하세요.
                </Typography>
                <Button variant="outlined" fullWidth sx={{ borderColor: '#ddd', color: '#666', '&:hover': { backgroundColor: '#b8191c', color: '#ffffff', borderColor: '#b8191c' } }}>
                  이벤트 보기
                </Button>
              </CardContent>
            </Card>

            <Card sx={{ flex: '1 1 300px', cursor: 'pointer', '&:hover': { transform: 'translateY(-5px)' }, transition: 'all 0.3s', backgroundColor: '#ffffff', border: '1px solid #ddd', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <CardContent sx={{ p: 4 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <EmojiEvents sx={{ mr: 2, fontSize: 32, color: '#b8191c' }} />
                  <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                    대회 참여
                  </Typography>
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  온라인/오프라인 대회에 참가하여 실력을 겨뤄보세요.
                </Typography>
                <Button variant="outlined" fullWidth sx={{ borderColor: '#ddd', color: '#666', '&:hover': { backgroundColor: '#b8191c', color: '#ffffff', borderColor: '#b8191c' } }}>
                  대회 신청
                </Button>
              </CardContent>
            </Card>
          </Box>
        </Container>
        </Box>

        {/* 격자형 게시물 섹션 */}
        <Box sx={{ backgroundColor: '#ffffff', py: 6 }}>
          <Container maxWidth="lg">
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', mb: 4, color: '#333333' }}>
              최신 소식
            </Typography>
            
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3 }}>
              {newsItems.map((item) => (
                <Box key={item.id} sx={{ flex: '1 1 300px', minWidth: '300px' }}>
                  <Card sx={{ cursor: 'pointer', '&:hover': { transform: 'translateY(-5px)' }, transition: 'all 0.3s', backgroundColor: '#ffffff', border: '1px solid #ddd', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
                    <CardMedia
                      component="img"
                      height="200"
                      image={item.image}
                      alt={item.title}
                    />
                    <CardContent>
                      <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold' }}>
                        {item.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {item.date}
                      </Typography>
                    </CardContent>
                  </Card>
                </Box>
              ))}
            </Box>
          </Container>
        </Box>


        {/* 푸터 */}
        <Box sx={{ backgroundColor: '#000000', py: 6 }}>
          <Container maxWidth="lg">
            {/* 상단 영역 */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap', gap: 4, mb: 4 }}>
              {/* 로고 및 소개 */}
              <Box sx={{ flex: '1 1 300px' }}>
                <Typography variant="h5" sx={{ color: '#ffffff', fontWeight: 'bold', mb: 2 }}>
                  ESTELA ⭐
                </Typography>
                <Typography variant="body2" sx={{ color: '#cccccc', mb: 3, lineHeight: 1.6 }}>
                  Lorecraft TCG Lounge에서 최고의 트레이딩 카드 게임 경험을 즐기세요. 
                  전략적인 게임플레이와 커뮤니티가 만나는 곳입니다.
                </Typography>
                {/* 소셜 아이콘 */}
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <IconButton sx={{ color: '#ffffff', '&:hover': { color: '#b8191c' } }}>
                    <YouTube />
                  </IconButton>
                  <IconButton sx={{ color: '#ffffff', '&:hover': { color: '#b8191c' } }}>
                    <Twitter />
                  </IconButton>
                  <IconButton sx={{ color: '#ffffff', '&:hover': { color: '#b8191c' } }}>
                    <Instagram />
                  </IconButton>
                  <IconButton sx={{ color: '#ffffff', '&:hover': { color: '#b8191c' } }}>
                    <Facebook />
                  </IconButton>
                </Box>
              </Box>

              {/* 링크 섹션 */}
              <Box sx={{ flex: '1 1 200px' }}>
                <Typography variant="h6" sx={{ color: '#ffffff', fontWeight: 'bold', mb: 2 }}>
                  게임 정보
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    놀이방법
                  </Button>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    카드 검색
                  </Button>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    룰북 다운로드
                  </Button>
                </Box>
              </Box>

              <Box sx={{ flex: '1 1 200px' }}>
                <Typography variant="h6" sx={{ color: '#ffffff', fontWeight: 'bold', mb: 2 }}>
                  커뮤니티
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    이벤트
                  </Button>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    대회 일정
                  </Button>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    플레이어 랭킹
                  </Button>
                </Box>
              </Box>

              <Box sx={{ flex: '1 1 200px' }}>
                <Typography variant="h6" sx={{ color: '#ffffff', fontWeight: 'bold', mb: 2 }}>
                  고객지원
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    FAQ
                  </Button>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    문의하기
                  </Button>
                  <Button sx={{ color: '#cccccc', justifyContent: 'flex-start', p: 0, '&:hover': { color: '#b8191c' } }}>
                    회사소개
                  </Button>
                </Box>
              </Box>
            </Box>
            
            {/* 하단 영역 */}
            <Box sx={{ pt: 3, borderTop: '1px solid #333', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
              <Typography variant="body2" sx={{ color: '#888888' }}>
                © 2024 Lorecraft. All rights reserved.
              </Typography>
              <Box sx={{ display: 'flex', gap: 3 }}>
                <Button sx={{ color: '#888888', p: 0, '&:hover': { color: '#b8191c' } }}>
                  개인정보처리방침
                </Button>
                <Button sx={{ color: '#888888', p: 0, '&:hover': { color: '#b8191c' } }}>
                  이용약관
                </Button>
              </Box>
            </Box>
          </Container>
        </Box>

      </Box>

      {/* 로그인 모달 */}
      <LoginModal 
        open={loginModalOpen} 
        onClose={() => setLoginModalOpen(false)} 
      />
    </Box>
  );
};

export default MainPage;