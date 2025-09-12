import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Chip,
  Tab,
  Tabs,
  List,
  ListItem,
  ListItemText,
  Divider,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import {
  EmojiEvents,
  LocationOn,
  People,
  AttachMoney,
  CalendarToday,
  Schedule
} from '@mui/icons-material';
import Navigation from '../components/Navigation';
import LoginModal from '../components/LoginModal';

const TournamentPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [region, setRegion] = useState('all');
  const [format, setFormat] = useState('all');
  const [loginModalOpen, setLoginModalOpen] = useState(false);

  const tournaments = [
    {
      id: 1,
      name: '서울 지역 챔피언십',
      date: '2024-08-30',
      time: '14:00',
      location: '강남 ESTELA 라운지',
      format: '스탠다드',
      maxParticipants: 64,
      currentParticipants: 45,
      entryFee: 10000,
      prizePool: '300만원',
      status: 'OPEN',
      description: '서울 지역 최고의 플레이어를 가리는 대회'
    },
    {
      id: 2,
      name: '초보자 친선 대회',
      date: '2024-08-27',
      time: '19:00',
      location: '홍대 TCG 센터',
      format: '리미티드',
      maxParticipants: 32,
      currentParticipants: 28,
      entryFee: 5000,
      prizePool: '50만원',
      status: 'OPEN',
      description: '초보자를 위한 친선 대회'
    },
    {
      id: 3,
      name: '월드 챔피언십 예선',
      date: '2024-09-15',
      time: '10:00',
      location: 'COEX 컨벤션센터',
      format: '스탠다드',
      maxParticipants: 256,
      currentParticipants: 189,
      entryFee: 20000,
      prizePool: '1000만원',
      status: 'OPEN',
      description: '월드 챔피언십 진출권을 걸고 벌이는 치열한 대회'
    },
    {
      id: 4,
      name: '부산 지역 리그',
      date: '2024-08-28',
      time: '13:00',
      location: '부산 해운대 매장',
      format: '모던',
      maxParticipants: 48,
      currentParticipants: 48,
      entryFee: 8000,
      prizePool: '150만원',
      status: 'FULL',
      description: '부산 지역 플레이어들의 정기 리그전'
    }
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'OPEN': return 'success';
      case 'FULL': return 'error';
      case 'CLOSED': return 'default';
      default: return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'OPEN': return '모집중';
      case 'FULL': return '마감';
      case 'CLOSED': return '종료';
      default: return '알 수 없음';
    }
  };

  return (
    <Box>
      <Navigation onLoginClick={() => setLoginModalOpen(true)} />

      <Container maxWidth="lg" sx={{ pt: 12, pb: 4 }}>
        <Box sx={{ mb: 4, textAlign: 'center' }}>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2, color: '#333' }}>
            대회예약
          </Typography>
          <Typography variant="body1" color="text.secondary">
            다양한 대회에 참가하여 실력을 겨뤄보세요
          </Typography>
        </Box>

        <Box sx={{ mb: 4, display: 'flex', gap: 2 }}>
          <FormControl sx={{ minWidth: 150 }}>
            <InputLabel>지역</InputLabel>
            <Select value={region} onChange={(e) => setRegion(e.target.value)} label="지역">
              <MenuItem value="all">전체</MenuItem>
              <MenuItem value="seoul">서울</MenuItem>
              <MenuItem value="busan">부산</MenuItem>
              <MenuItem value="online">온라인</MenuItem>
            </Select>
          </FormControl>
          
          <FormControl sx={{ minWidth: 150 }}>
            <InputLabel>형식</InputLabel>
            <Select value={format} onChange={(e) => setFormat(e.target.value)} label="형식">
              <MenuItem value="all">전체</MenuItem>
              <MenuItem value="standard">스탠다드</MenuItem>
              <MenuItem value="modern">모던</MenuItem>
              <MenuItem value="limited">리미티드</MenuItem>
            </Select>
          </FormControl>
        </Box>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {tournaments.map((tournament) => (
            <Box key={tournament.id} sx={{ flex: '1 1 100%' }}>
              <Card sx={{ 
                cursor: 'pointer',
                '&:hover': { 
                  transform: 'translateY(-3px)',
                  boxShadow: '0 6px 20px rgba(0,0,0,0.1)'
                },
                transition: 'all 0.3s'
              }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box>
                      <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 1 }}>
                        {tournament.name}
                      </Typography>
                      <Typography variant="body1" color="text.secondary" sx={{ mb: 2 }}>
                        {tournament.description}
                      </Typography>
                    </Box>
                    <Chip 
                      label={getStatusLabel(tournament.status)}
                      color={getStatusColor(tournament.status) as any}
                    />
                  </Box>
                  
                  <Box sx={{ display: 'flex', gap: 2, flexDirection: { xs: 'column', md: 'row' } }}>
                    <Box sx={{ flex: '2 1 auto' }}>
                      <List dense>
                        <ListItem sx={{ px: 0 }}>
                          <CalendarToday sx={{ mr: 2, color: 'text.secondary' }} />
                          <ListItemText 
                            primary="날짜 & 시간" 
                            secondary={`${tournament.date} ${tournament.time}`}
                          />
                        </ListItem>
                        <ListItem sx={{ px: 0 }}>
                          <LocationOn sx={{ mr: 2, color: 'text.secondary' }} />
                          <ListItemText 
                            primary="장소" 
                            secondary={tournament.location}
                          />
                        </ListItem>
                        <ListItem sx={{ px: 0 }}>
                          <EmojiEvents sx={{ mr: 2, color: 'text.secondary' }} />
                          <ListItemText 
                            primary="형식" 
                            secondary={tournament.format}
                          />
                        </ListItem>
                        <ListItem sx={{ px: 0 }}>
                          <People sx={{ mr: 2, color: 'text.secondary' }} />
                          <ListItemText 
                            primary="참가자" 
                            secondary={`${tournament.currentParticipants}/${tournament.maxParticipants}명`}
                          />
                        </ListItem>
                      </List>
                    </Box>
                    
                    <Box sx={{ flex: '1 1 300px', minWidth: '300px' }}>
                      <Box sx={{ textAlign: 'center', p: 2, bgcolor: '#f5f5f5', borderRadius: 2 }}>
                        <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#b8191c' }}>
                          상금
                        </Typography>
                        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 1 }}>
                          {tournament.prizePool}
                        </Typography>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                          참가비: ₩{tournament.entryFee.toLocaleString()}
                        </Typography>
                        <Button 
                          variant="contained" 
                          fullWidth
                          disabled={tournament.status === 'FULL'}
                          sx={{ 
                            backgroundColor: tournament.status === 'FULL' ? '#ccc' : '#b8191c',
                            '&:hover': { backgroundColor: tournament.status === 'FULL' ? '#ccc' : '#a01018' }
                          }}
                        >
                          {tournament.status === 'OPEN' ? '참가 신청' : '마감됨'}
                        </Button>
                      </Box>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </Box>
          ))}
        </Box>
      </Container>

      {/* 로그인 모달 */}
      <LoginModal 
        open={loginModalOpen} 
        onClose={() => setLoginModalOpen(false)} 
      />
    </Box>
  );
};

export default TournamentPage;