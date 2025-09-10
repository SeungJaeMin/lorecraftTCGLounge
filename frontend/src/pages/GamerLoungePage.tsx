import React, { useEffect, useState } from 'react';
import {
  Container,
  Typography,
  Box,
  AppBar,
  Toolbar,
  Button,
  Card,
  CardContent,
  Avatar,
  Chip,
  LinearProgress,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Paper,
  Tabs,
  Tab,
  Badge,
  CircularProgress
} from '@mui/material';
import {
  Dashboard,
  EmojiEvents,
  CardGiftcard,
  Event,
  TrendingUp,
  Person,
  Logout,
  NotificationsActive,
  Mail,
  Star,
  MilitaryTech,
  LocalFireDepartment
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { gamerAPI, authAPI } from '../services/api';

const GamerLoungePage: React.FC = () => {
  const navigate = useNavigate();
  const [tabValue, setTabValue] = React.useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // 모든 useState 훅을 컴포너트 상단에 선언
  const [userData, setUserData] = useState({
    username: '',
    nickname: '',
    rank: '',
    winRate: 0,
    totalGames: 0,
    totalWins: 0,
    totalLosses: 0,
    currentRating: 0,
    usablePoint: 0
  });
  const [myDecks, setMyDecks] = useState<any[]>([]);
  const [recentMatches, setRecentMatches] = useState<any[]>([]);
  const [upcomingTournaments, setUpcomingTournaments] = useState<any[]>([]);

  const handleLogout = async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('userType');
      localStorage.removeItem('username');
      navigate('/login');
    }
  };

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        const response = await gamerAPI.getDashboard();
        
        if (response.data.success) {
          const data = response.data.data;
          
          // 프로필 데이터 설정
          setUserData({
            username: data.profile.username || '',
            nickname: data.profile.nickname || '게이머',
            rank: data.profile.rank || 'BRONZE',
            winRate: Math.round(data.profile.winRate || 0),
            totalGames: data.profile.totalGames || 0,
            totalWins: data.profile.totalWins || 0,
            totalLosses: data.profile.totalLosses || 0,
            currentRating: data.profile.currentRating || 0,
            usablePoint: data.profile.usablePoint || 0
          });
          
          // 덱, 경기, 대회 데이터 설정
          setMyDecks(data.myDecks || []);
          setRecentMatches(data.recentMatches || []);
          setUpcomingTournaments(data.upcomingTournaments || []);
        } else {
          setError(response.data.message || '데이터를 불러오는데 실패했습니다.');
        }
      } catch (error: any) {
        console.error('Dashboard data fetch error:', error);
        setError('대시보드 데이터 로드 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '100vh'
      }}>
        <CircularProgress size={50} />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '100vh',
        flexDirection: 'column',
        gap: 2
      }}>
        <Typography variant="h6" color="error">{error}</Typography>
        <Button variant="contained" onClick={() => window.location.reload()}>
          다시 시도
        </Button>
      </Box>
    );
  }

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  return (
    <Box sx={{ bgcolor: '#f5f5f5', minHeight: '100vh' }}>
      {/* 네비게이션 바 */}
      <AppBar position="fixed" sx={{ backgroundColor: '#1a1a1a' }}>
        <Toolbar sx={{ minHeight: '80px !important', height: '80px' }}>
          <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#f4c87a' }}>
            ESTELA ⭐ 게이머 라운지
          </Typography>
          
          <Box sx={{ flexGrow: 1, display: 'flex', justifyContent: 'center', gap: 3 }}>
            <Button sx={{ color: 'white' }} startIcon={<Dashboard />}>대시보드</Button>
            <Button sx={{ color: 'white' }} startIcon={<CardGiftcard />}>내 덱</Button>
            <Button sx={{ color: 'white' }} startIcon={<EmojiEvents />}>대회</Button>
            <Button sx={{ color: 'white' }} startIcon={<Event />}>일정</Button>
            <Button sx={{ color: 'white' }} startIcon={<TrendingUp />}>랭킹</Button>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            <Badge badgeContent={3} color="error">
              <NotificationsActive sx={{ color: 'white', cursor: 'pointer' }} />
            </Badge>
            <Badge badgeContent={5} color="primary">
              <Mail sx={{ color: 'white', cursor: 'pointer' }} />
            </Badge>
            <Button 
              startIcon={<Person />} 
              sx={{ color: 'white', borderColor: 'white' }}
              variant="outlined"
            >
              {userData.nickname}
            </Button>
            <Button 
              onClick={handleLogout}
              startIcon={<Logout />} 
              sx={{ color: '#ff6b6b' }}
            >
              로그아웃
            </Button>
          </Box>
        </Toolbar>
      </AppBar>

      {/* 메인 콘텐츠 */}
      <Container maxWidth="xl" sx={{ pt: 12, pb: 4 }}>
        {/* 유저 정보 카드 */}
        <Box sx={{ display: 'flex', gap: 3, mb: 4, flexWrap: 'wrap' }}>
          <Box sx={{ flex: '1 1 300px' }}>
            <Card sx={{ 
              background: '#333333',
              color: 'white',
              height: '100%'
            }}>
              <CardContent sx={{ p: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
                  <Avatar 
                    sx={{ 
                      width: 80, 
                      height: 80, 
                      bgcolor: 'rgba(255,255,255,0.2)',
                      border: '3px solid white'
                    }}
                  >
                    <Star sx={{ fontSize: 40 }} />
                  </Avatar>
                  <Box>
                    <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                      {userData.nickname}
                    </Typography>
                    <Chip 
                      label={userData.rank} 
                      sx={{ 
                        bgcolor: 'rgba(255,255,255,0.2)', 
                        color: 'white',
                        mt: 1
                      }} 
                    />
                  </Box>
                </Box>
                <Box sx={{ mt: 3 }}>
                  <Typography variant="body2" sx={{ opacity: 0.9 }}>
                    포인트
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#f4c87a', mt: 1 }}>
                    {userData.usablePoint?.toLocaleString() || 0} P
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box sx={{ flex: '1 1 300px' }}>
            <Card sx={{ height: '100%' }}>
              <CardContent sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                  전적 통계
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'space-around', mb: 2 }}>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                      {userData.totalWins}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      승리
                    </Typography>
                  </Box>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#f44336' }}>
                      {userData.totalLosses}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      패배
                    </Typography>
                  </Box>
                  <Box sx={{ textAlign: 'center' }}>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#2196f3' }}>
                      {userData.winRate}%
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      승률
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ mt: 3, p: 2, bgcolor: '#f5f5f5', borderRadius: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    현재 레이팅
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#b8191c' }}>
                    {userData.currentRating} RP
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box sx={{ flex: '1 1 300px' }}>
            <Card sx={{ 
              height: '100%',
              background: '#b8191c',
              color: 'white'
            }}>
              <CardContent sx={{ p: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                  현재 랭크
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', py: 2 }}>
                  <MilitaryTech sx={{ fontSize: 80 }} />
                </Box>
                <Typography variant="h4" sx={{ textAlign: 'center', fontWeight: 'bold' }}>
                  {userData.rank}
                </Typography>
                <Typography variant="body2" sx={{ textAlign: 'center', mt: 1, opacity: 0.9 }}>
                  상위 5% 플레이어
                </Typography>
              </CardContent>
            </Card>
          </Box>
        </Box>

        {/* 탭 섹션 */}
        <Paper sx={{ mb: 3 }}>
          <Tabs value={tabValue} onChange={handleTabChange} sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tab label="내 덱" />
            <Tab label="대회 일정" />
            <Tab label="최근 경기" />
          </Tabs>
        </Paper>

        {/* 탭 콘텐츠 */}
        {tabValue === 0 && (
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {myDecks.map((deck) => (
              <Box key={deck.id} sx={{ flex: '1 1 300px' }}>
                <Card sx={{ 
                  cursor: 'pointer',
                  '&:hover': { transform: 'translateY(-5px)', transition: 'all 0.3s' }
                }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                        {deck.name}
                      </Typography>
                      <LocalFireDepartment sx={{ color: '#f4c87a' }} />
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="body2" color="text.secondary">
                        승률
                      </Typography>
                      <Typography variant="body2" sx={{ fontWeight: 'bold', color: deck.winRate >= 60 ? '#4caf50' : '#ff9800' }}>
                        {deck.winRate}%
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="body2" color="text.secondary">
                        총 게임 수
                      </Typography>
                      <Typography variant="body2">
                        {deck.games}
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                      <Typography variant="body2" color="text.secondary">
                        마지막 플레이
                      </Typography>
                      <Typography variant="body2">
                        {deck.lastPlayed}
                      </Typography>
                    </Box>
                    <Button 
                      fullWidth 
                      variant="outlined" 
                      sx={{ 
                        mt: 2,
                        borderColor: '#b8191c',
                        color: '#b8191c',
                        '&:hover': { 
                          backgroundColor: '#b8191c',
                          color: 'white'
                        }
                      }}
                    >
                      덱 편집
                    </Button>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        )}

        {tabValue === 1 && (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            {upcomingTournaments.map((tournament) => (
              <Box key={tournament.id}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box>
                        <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                          {tournament.name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {tournament.date}
                        </Typography>
                      </Box>
                      <Box sx={{ textAlign: 'right' }}>
                        <Typography variant="h6" sx={{ color: '#f4c87a', fontWeight: 'bold' }}>
                          {tournament.prize}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          참가자: {tournament.participants}
                        </Typography>
                      </Box>
                      <Button 
                        variant="contained" 
                        sx={{ 
                          backgroundColor: '#b8191c',
                          '&:hover': { backgroundColor: '#a01018' }
                        }}
                      >
                        참가 신청
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        )}

        {tabValue === 2 && (
          <Card>
            <CardContent>
              <List>
                {recentMatches.map((match) => (
                  <ListItem key={match.id} sx={{ borderBottom: '1px solid #eee' }}>
                    <ListItemIcon>
                      {match.result === 'WIN' ? (
                        <EmojiEvents sx={{ color: '#4caf50' }} />
                      ) : (
                        <EmojiEvents sx={{ color: '#f44336' }} />
                      )}
                    </ListItemIcon>
                    <ListItemText
                      primary={
                        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                          <Typography>
                            vs {match.opponent}
                          </Typography>
                          <Chip 
                            label={match.result} 
                            size="small"
                            sx={{ 
                              backgroundColor: match.result === 'WIN' ? '#4caf50' : '#f44336',
                              color: 'white'
                            }}
                          />
                        </Box>
                      }
                      secondary={
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
                          <Typography variant="body2" color="text.secondary">
                            덱: {match.deck}
                          </Typography>
                          <Typography 
                            variant="body2" 
                            sx={{ 
                              color: match.ratingChange.startsWith('+') ? '#4caf50' : '#f44336',
                              fontWeight: 'bold'
                            }}
                          >
                            {match.ratingChange} RP
                          </Typography>
                        </Box>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </Card>
        )}
      </Container>
    </Box>
  );
};

export default GamerLoungePage;