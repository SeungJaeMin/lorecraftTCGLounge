import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Avatar,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Tab,
  Tabs,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  LinearProgress,
  Chip,
  Divider
} from '@mui/material';
import {
  EmojiEvents,
  TrendingUp,
  TrendingDown,
  Star,
  MilitaryTech,
  LocalFireDepartment
} from '@mui/icons-material';
import Navigation from '../components/Navigation';

const RankingPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);

  const topPlayers = [
    {
      rank: 1,
      username: '드래곤마스터',
      rating: 2847,
      winRate: 78.5,
      totalGames: 342,
      tier: 'MASTER',
      change: 'up'
    },
    {
      rank: 2,
      username: '카드킹',
      rating: 2756,
      winRate: 74.2,
      totalGames: 298,
      tier: 'MASTER',
      change: 'down'
    },
    {
      rank: 3,
      username: '프로게이머',
      rating: 2698,
      winRate: 72.1,
      totalGames: 276,
      tier: 'MASTER',
      change: 'up'
    },
    {
      rank: 4,
      username: '매직유저',
      rating: 2634,
      winRate: 69.8,
      totalGames: 254,
      tier: 'DIAMOND',
      change: 'same'
    },
    {
      rank: 5,
      username: '엘리트플레이어',
      rating: 2587,
      winRate: 68.4,
      totalGames: 289,
      tier: 'DIAMOND',
      change: 'up'
    }
  ];

  const weeklyRanking = [
    { rank: 1, username: '신예강자', gamesWon: 28, winRate: 87.5 },
    { rank: 2, username: '주간챔피언', gamesWon: 25, winRate: 83.3 },
    { rank: 3, username: '라이징스타', gamesWon: 22, winRate: 78.6 },
    { rank: 4, username: '위클리킹', gamesWon: 20, winRate: 76.9 },
    { rank: 5, username: '주말전사', gamesWon: 18, winRate: 75.0 }
  ];

  const getTierColor = (tier: string) => {
    switch (tier) {
      case 'MASTER': return '#ff9800';
      case 'DIAMOND': return '#2196f3';
      case 'PLATINUM': return '#4caf50';
      case 'GOLD': return '#ffeb3b';
      default: return '#9e9e9e';
    }
  };

  const getTierIcon = (tier: string) => {
    switch (tier) {
      case 'MASTER': return <MilitaryTech />;
      case 'DIAMOND': return <Star />;
      default: return <LocalFireDepartment />;
    }
  };

  const getRankIcon = (rank: number) => {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return `#${rank}`;
  };

  return (
    <Box>
      <Navigation />

      <Container maxWidth="lg" sx={{ pt: 12, pb: 4 }}>
        <Box sx={{ mb: 4, textAlign: 'center' }}>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2, color: '#333' }}>
            플레이어 랭킹
          </Typography>
          <Typography variant="body1" color="text.secondary">
            ESTELA TCG 최고의 플레이어들을 만나보세요
          </Typography>
        </Box>

        <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} sx={{ borderBottom: 1, borderColor: 'divider', mb: 4 }}>
          <Tab label="전체 랭킹" />
          <Tab label="주간 랭킹" />
          <Tab label="티어 분포" />
        </Tabs>

        {tabValue === 0 && (
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ flex: '1 1 100%' }}>
              <Card>
                <CardContent>
                  <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
                    TOP 50 플레이어
                  </Typography>
                  <TableContainer>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>순위</TableCell>
                          <TableCell>플레이어</TableCell>
                          <TableCell>티어</TableCell>
                          <TableCell align="center">레이팅</TableCell>
                          <TableCell align="center">승률</TableCell>
                          <TableCell align="center">총 경기</TableCell>
                          <TableCell align="center">변동</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {topPlayers.map((player) => (
                          <TableRow key={player.rank} hover>
                            <TableCell>
                              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                  {getRankIcon(player.rank)}
                                </Typography>
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <Avatar sx={{ mr: 2, bgcolor: getTierColor(player.tier) }}>
                                  {getTierIcon(player.tier)}
                                </Avatar>
                                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                  {player.username}
                                </Typography>
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Chip 
                                label={player.tier}
                                sx={{ 
                                  backgroundColor: getTierColor(player.tier),
                                  color: 'white'
                                }}
                              />
                            </TableCell>
                            <TableCell align="center">
                              <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#b8191c' }}>
                                {player.rating}
                              </Typography>
                            </TableCell>
                            <TableCell align="center">
                              <Typography sx={{ fontWeight: 'bold', color: player.winRate >= 70 ? '#4caf50' : '#ff9800' }}>
                                {player.winRate}%
                              </Typography>
                            </TableCell>
                            <TableCell align="center">
                              {player.totalGames}
                            </TableCell>
                            <TableCell align="center">
                              {player.change === 'up' && <TrendingUp sx={{ color: '#4caf50' }} />}
                              {player.change === 'down' && <TrendingDown sx={{ color: '#f44336' }} />}
                              {player.change === 'same' && <Typography>-</Typography>}
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </CardContent>
              </Card>
            </Box>
          </Box>
        )}

        {tabValue === 1 && (
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ flex: '1 1 100%' }}>
              <Card>
                <CardContent>
                  <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
                    이번 주 최고 성과
                  </Typography>
                  <List>
                    {weeklyRanking.map((player, index) => (
                      <React.Fragment key={player.rank}>
                        <ListItem>
                          <ListItemAvatar>
                            <Avatar sx={{ 
                              bgcolor: index < 3 ? '#f4c87a' : '#e0e0e0',
                              color: index < 3 ? 'white' : 'black',
                              fontWeight: 'bold'
                            }}>
                              {getRankIcon(player.rank)}
                            </Avatar>
                          </ListItemAvatar>
                          <ListItemText
                            primary={
                              <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                {player.username}
                              </Typography>
                            }
                            secondary={
                              <Box sx={{ display: 'flex', gap: 2, mt: 1 }}>
                                <Chip label={`${player.gamesWon}승`} size="small" color="primary" />
                                <Chip label={`승률 ${player.winRate}%`} size="small" color="success" />
                              </Box>
                            }
                          />
                        </ListItem>
                        {index < weeklyRanking.length - 1 && <Divider />}
                      </React.Fragment>
                    ))}
                  </List>
                </CardContent>
              </Card>
            </Box>
          </Box>
        )}

        {tabValue === 2 && (
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ flex: '1 1 100%' }}>
              <Card>
                <CardContent>
                  <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
                    티어 분포
                  </Typography>
                  
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    {[
                      { tier: 'MASTER', players: 234, percentage: 2.1, color: '#ff9800' },
                      { tier: 'DIAMOND', players: 1567, percentage: 14.2, color: '#2196f3' },
                      { tier: 'PLATINUM', players: 3421, percentage: 31.0, color: '#4caf50' },
                      { tier: 'GOLD', players: 4532, percentage: 41.1, color: '#ffeb3b' },
                      { tier: 'SILVER', players: 1278, percentage: 11.6, color: '#9e9e9e' }
                    ].map((tierData) => (
                      <Box key={tierData.tier} sx={{ flex: '1 1 100%' }}>
                        <Box sx={{ mb: 2 }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                              <Avatar sx={{ mr: 2, bgcolor: tierData.color, width: 32, height: 32 }}>
                                {getTierIcon(tierData.tier)}
                              </Avatar>
                              <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                {tierData.tier}
                              </Typography>
                            </Box>
                            <Typography variant="body1">
                              {tierData.players.toLocaleString()}명 ({tierData.percentage}%)
                            </Typography>
                          </Box>
                          <LinearProgress 
                            variant="determinate" 
                            value={tierData.percentage} 
                            sx={{ 
                              height: 10, 
                              borderRadius: 5,
                              bgcolor: '#e0e0e0',
                              '& .MuiLinearProgress-bar': {
                                bgcolor: tierData.color
                              }
                            }}
                          />
                        </Box>
                      </Box>
                    ))}
                  </Box>
                </CardContent>
              </Card>
            </Box>
          </Box>
        )}
      </Container>
    </Box>
  );
};

export default RankingPage;