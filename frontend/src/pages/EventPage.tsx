import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Card,
  CardContent,
  CardMedia,
  Grid,
  Chip,
  Tab,
  Tabs,
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Divider
} from '@mui/material';
import {
  Event,
  Schedule,
  EmojiEvents,
  CardGiftcard,
  LocalOffer,
  CalendarToday,
  LocationOn,
  People
} from '@mui/icons-material';
import Navigation from '../components/Navigation';

const EventPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);

  const events = [
    {
      id: 1,
      title: '여름 대축제 이벤트',
      description: '8월 한 달간 진행되는 특별 이벤트! 다양한 보상을 획득하세요.',
      image: 'https://via.placeholder.com/400x250/fa709a/ffffff?text=여름+대축제',
      type: 'SEASONAL',
      status: 'ACTIVE',
      startDate: '2024-08-01',
      endDate: '2024-08-31',
      location: '온라인',
      participants: '15,234명 참여중',
      rewards: ['한정판 카드팩', '프리미엄 슬리브', '특별 칭호']
    },
    {
      id: 2,
      title: '월드 챔피언십 예선',
      description: '전 세계 최강을 가리는 대회의 예선전이 시작됩니다.',
      image: 'https://via.placeholder.com/400x250/667eea/ffffff?text=월드+챔피언십',
      type: 'TOURNAMENT',
      status: 'UPCOMING',
      startDate: '2024-09-01',
      endDate: '2024-09-30',
      location: '전국 매장',
      participants: '등록 시작 예정',
      rewards: ['우승 트로피', '상금 1000만원', '월드대회 진출권']
    },
    {
      id: 3,
      title: '신규 확장팩 프리릴리즈',
      description: '"드래곤의 귀환" 확장팩을 미리 체험해보세요.',
      image: 'https://via.placeholder.com/400x250/4facfe/ffffff?text=프리릴리즈',
      type: 'RELEASE',
      status: 'ENDED',
      startDate: '2024-08-20',
      endDate: '2024-08-22',
      location: '전국 매장',
      participants: '3,456명 참여',
      rewards: ['프로모 카드', '확장팩 6팩', '기념품']
    }
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'UPCOMING': return 'warning';
      case 'ENDED': return 'default';
      default: return 'default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'ACTIVE': return '진행중';
      case 'UPCOMING': return '예정';
      case 'ENDED': return '종료';
      default: return '알 수 없음';
    }
  };

  return (
    <Box>
      <Navigation />

      <Container maxWidth="lg" sx={{ pt: 12, pb: 4 }}>
        <Box sx={{ mb: 4, textAlign: 'center' }}>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2, color: '#333' }}>
            이벤트
          </Typography>
          <Typography variant="body1" color="text.secondary">
            다양한 이벤트에 참여하고 특별한 보상을 획득하세요
          </Typography>
        </Box>

        <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} sx={{ borderBottom: 1, borderColor: 'divider', mb: 4 }}>
          <Tab label="전체" />
          <Tab label="진행중" />
          <Tab label="예정" />
        </Tabs>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {events.map((event) => (
            <Box key={event.id} sx={{ flex: '1 1 100%' }}>
              <Card sx={{ 
                cursor: 'pointer',
                '&:hover': { 
                  transform: 'translateY(-3px)',
                  boxShadow: '0 6px 20px rgba(0,0,0,0.1)'
                },
                transition: 'all 0.3s'
              }}>
                <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' } }}>
                  <Box sx={{ flex: '0 0 350px', minWidth: { xs: '100%', md: '350px' } }}>
                    <CardMedia
                      component="img"
                      height="250"
                      image={event.image}
                      alt={event.title}
                      sx={{ objectFit: 'cover' }}
                    />
                  </Box>
                  <Box sx={{ flex: '1 1 auto' }}>
                    <CardContent sx={{ height: '250px', display: 'flex', flexDirection: 'column' }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <Typography variant="h5" sx={{ fontWeight: 'bold', mr: 2 }}>
                          {event.title}
                        </Typography>
                        <Chip 
                          label={getStatusLabel(event.status)}
                          color={getStatusColor(event.status) as any}
                          size="small"
                        />
                      </Box>
                      
                      <Typography variant="body1" sx={{ mb: 2, flexGrow: 1 }}>
                        {event.description}
                      </Typography>
                      
                      <List dense>
                        <ListItem sx={{ px: 0 }}>
                          <ListItemAvatar>
                            <Avatar sx={{ bgcolor: '#b8191c', width: 32, height: 32 }}>
                              <CalendarToday sx={{ fontSize: 16 }} />
                            </Avatar>
                          </ListItemAvatar>
                          <ListItemText 
                            primary="기간" 
                            secondary={`${event.startDate} ~ ${event.endDate}`}
                          />
                        </ListItem>
                        <ListItem sx={{ px: 0 }}>
                          <ListItemAvatar>
                            <Avatar sx={{ bgcolor: '#4caf50', width: 32, height: 32 }}>
                              <LocationOn sx={{ fontSize: 16 }} />
                            </Avatar>
                          </ListItemAvatar>
                          <ListItemText 
                            primary="장소" 
                            secondary={event.location}
                          />
                        </ListItem>
                        <ListItem sx={{ px: 0 }}>
                          <ListItemAvatar>
                            <Avatar sx={{ bgcolor: '#2196f3', width: 32, height: 32 }}>
                              <People sx={{ fontSize: 16 }} />
                            </Avatar>
                          </ListItemAvatar>
                          <ListItemText 
                            primary="참여현황" 
                            secondary={event.participants}
                          />
                        </ListItem>
                      </List>
                      
                      <Box sx={{ mt: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                          {event.rewards.slice(0, 2).map((reward, idx) => (
                            <Chip key={idx} label={reward} size="small" variant="outlined" />
                          ))}
                          {event.rewards.length > 2 && (
                            <Chip label={`+${event.rewards.length - 2}개`} size="small" variant="outlined" />
                          )}
                        </Box>
                        <Button 
                          variant="contained" 
                          disabled={event.status === 'ENDED'}
                          sx={{ 
                            backgroundColor: event.status === 'ENDED' ? '#ccc' : '#b8191c',
                            '&:hover': { backgroundColor: event.status === 'ENDED' ? '#ccc' : '#a01018' }
                          }}
                        >
                          {event.status === 'ACTIVE' ? '참여하기' : event.status === 'UPCOMING' ? '알림 신청' : '종료됨'}
                        </Button>
                      </Box>
                    </CardContent>
                  </Box>
                </Box>
              </Card>
            </Box>
          ))}
        </Box>
      </Container>
    </Box>
  );
};

export default EventPage;