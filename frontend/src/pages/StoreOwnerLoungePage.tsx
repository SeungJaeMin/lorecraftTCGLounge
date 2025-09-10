import React from 'react';
import {
  Container,
  Typography,
  Box,
  AppBar,
  Toolbar,
  Button,
  Card,
  CardContent,
  Grid,
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
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Divider
} from '@mui/material';
import {
  Dashboard,
  Store,
  Event,
  ShoppingCart,
  People,
  Assessment,
  Person,
  Logout,
  NotificationsActive,
  Mail,
  CheckCircle,
  Cancel,
  Schedule,
  TrendingUp,
  TrendingDown,
  AttachMoney,
  Inventory,
  LocalShipping,
  Edit,
  Add,
  CalendarToday,
  Groups,
  EmojiEvents
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const StoreOwnerLoungePage: React.FC = () => {
  const navigate = useNavigate();
  const [tabValue, setTabValue] = React.useState(0);

  const handleLogout = () => {
    navigate('/');
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  // TODO: API에서 점주 데이터 가져오기
  const storeData = {
    storeName: '',
    ownerName: '',
    location: '',
    monthlyRevenue: 0,
    monthlyOrders: 0,
    activeEvents: 0,
    totalPlayers: 0,
    todayCheckIns: 0,
    pendingOrders: 0
  };

  // TODO: API에서 오늘의 대회 데이터 가져오기
  const todayTournaments: any[] = [];

  // TODO: API에서 체크인 대기 목록 가져오기
  const checkInQueue: any[] = [];

  // TODO: API에서 최근 주문 데이터 가져오기
  const recentOrders: any[] = [];

  // TODO: API에서 재고 현황 가져오기
  const inventory: any[] = [];

  // TODO: API에서 이번 달 대회 일정 가져오기
  const monthlyTournaments: any[] = [];

  return (
    <Box sx={{ bgcolor: '#f5f5f5', minHeight: '100vh' }}>
      {/* 네비게이션 바 */}
      <AppBar position="fixed" sx={{ backgroundColor: '#1a1a1a' }}>
        <Toolbar sx={{ minHeight: '80px !important', height: '80px' }}>
          <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#f4c87a' }}>
            ESTELA ⭐ StoreOwner Lounge
          </Typography>
          
          <Box sx={{ flexGrow: 1, display: 'flex', justifyContent: 'center', gap: 3 }}>
            <Button sx={{ color: 'white' }} startIcon={<Dashboard />}>대시보드</Button>
            <Button sx={{ color: 'white' }} startIcon={<Event />}>대회 관리</Button>
            <Button sx={{ color: 'white' }} startIcon={<ShoppingCart />}>주문 관리</Button>
            <Button sx={{ color: 'white' }} startIcon={<Inventory />}>재고 관리</Button>
            <Button sx={{ color: 'white' }} startIcon={<Assessment />}>매출 분석</Button>
            <Button sx={{ color: 'white' }} startIcon={<People />}>회원 관리</Button>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            <Badge badgeContent={storeData.pendingOrders} color="error">
              <NotificationsActive sx={{ color: 'white', cursor: 'pointer' }} />
            </Badge>
            <Badge badgeContent={2} color="primary">
              <Mail sx={{ color: 'white', cursor: 'pointer' }} />
            </Badge>
            <Button 
              startIcon={<Store />} 
              sx={{ color: 'white', borderColor: 'white' }}
              variant="outlined"
            >
              {storeData.storeName}
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
        {/* 통계 카드 */}
        <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap', mb: 4 }}>
          <Box sx={{ flex: '1 1 300px', minWidth: '300px' }}>
            <Card sx={{ 
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white'
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box>
                    <Typography variant="body2" sx={{ opacity: 0.9 }}>
                      이번 달 매출
                    </Typography>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', mt: 1 }}>
                      ₩{storeData.monthlyRevenue.toLocaleString()}
                    </Typography>
                    <Chip 
                      icon={<TrendingUp />}
                      label="+12.5%" 
                      size="small"
                      sx={{ 
                        mt: 1,
                        bgcolor: 'rgba(255,255,255,0.2)', 
                        color: 'white'
                      }} 
                    />
                  </Box>
                  <AttachMoney sx={{ fontSize: 40, opacity: 0.7 }} />
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box sx={{ flex: '1 1 300px', minWidth: '300px' }}>
            <Card sx={{ 
              background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
              color: 'white'
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box>
                    <Typography variant="body2" sx={{ opacity: 0.9 }}>
                      오늘 체크인
                    </Typography>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', mt: 1 }}>
                      {storeData.todayCheckIns}명
                    </Typography>
                    <Typography variant="caption" sx={{ opacity: 0.9 }}>
                      대기: {checkInQueue.length}명
                    </Typography>
                  </Box>
                  <Groups sx={{ fontSize: 40, opacity: 0.7 }} />
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box sx={{ flex: '1 1 300px', minWidth: '300px' }}>
            <Card sx={{ 
              background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
              color: 'white'
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box>
                    <Typography variant="body2" sx={{ opacity: 0.9 }}>
                      활성 이벤트
                    </Typography>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', mt: 1 }}>
                      {storeData.activeEvents}개
                    </Typography>
                    <Typography variant="caption" sx={{ opacity: 0.9 }}>
                      오늘: {todayTournaments.length}개
                    </Typography>
                  </Box>
                  <EmojiEvents sx={{ fontSize: 40, opacity: 0.7 }} />
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box sx={{ flex: '1 1 300px', minWidth: '300px' }}>
            <Card sx={{ 
              background: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
              color: 'white'
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box>
                    <Typography variant="body2" sx={{ opacity: 0.9 }}>
                      대기 주문
                    </Typography>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', mt: 1 }}>
                      {storeData.pendingOrders}건
                    </Typography>
                    <Typography variant="caption" sx={{ opacity: 0.9 }}>
                      총액: ₩705,000
                    </Typography>
                  </Box>
                  <LocalShipping sx={{ fontSize: 40, opacity: 0.7 }} />
                </Box>
              </CardContent>
            </Card>
          </Box>
        </Box>

        {/* 탭 섹션 */}
        <Paper sx={{ mb: 3 }}>
          <Tabs value={tabValue} onChange={handleTabChange} sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tab label="오늘의 대회" />
            <Tab label="체크인 관리" />
            <Tab label="주문 현황" />
            <Tab label="재고 관리" />
            <Tab label="대회 일정" />
          </Tabs>
        </Paper>

        {/* 탭 콘텐츠 */}
        {tabValue === 0 && (
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {todayTournaments.map((tournament) => (
              <Box key={tournament.id} sx={{ flex: '1 1 400px', minWidth: '400px' }}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                      <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                        {tournament.name}
                      </Typography>
                      <Chip 
                        label={tournament.status === 'IN_PROGRESS' ? '진행중' : '예정'} 
                        color={tournament.status === 'IN_PROGRESS' ? 'success' : 'default'}
                        size="small"
                      />
                    </Box>
                    <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                      <Box sx={{ flex: '1 1 150px' }}>
                        <Typography variant="body2" color="text.secondary">시작 시간</Typography>
                        <Typography variant="body1">{tournament.time}</Typography>
                      </Box>
                      <Box sx={{ flex: '1 1 150px' }}>
                        <Typography variant="body2" color="text.secondary">참가자</Typography>
                        <Typography variant="body1">{tournament.participants}</Typography>
                      </Box>
                      <Box sx={{ flex: '1 1 150px' }}>
                        <Typography variant="body2" color="text.secondary">상금</Typography>
                        <Typography variant="body1" sx={{ color: '#f4c87a', fontWeight: 'bold' }}>
                          {tournament.prizePool}
                        </Typography>
                      </Box>
                      <Box sx={{ flex: '1 1 150px' }}>
                        <Box sx={{ mt: 1 }}>
                          <Button 
                            fullWidth
                            variant="contained"
                            size="small"
                            sx={{ 
                              backgroundColor: '#b8191c',
                              '&:hover': { backgroundColor: '#a01018' }
                            }}
                          >
                            대회 관리
                          </Button>
                        </Box>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        )}

        {tabValue === 1 && (
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  체크인 대기 목록
                </Typography>
                <Button 
                  startIcon={<CheckCircle />}
                  variant="contained"
                  sx={{ 
                    backgroundColor: '#4caf50',
                    '&:hover': { backgroundColor: '#45a049' }
                  }}
                >
                  전체 승인
                </Button>
              </Box>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>플레이어</TableCell>
                      <TableCell>대회</TableCell>
                      <TableCell>도착 시간</TableCell>
                      <TableCell>상태</TableCell>
                      <TableCell align="center">액션</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {checkInQueue.map((player) => (
                      <TableRow key={player.id}>
                        <TableCell>{player.playerName}</TableCell>
                        <TableCell>{player.tournament}</TableCell>
                        <TableCell>{player.time}</TableCell>
                        <TableCell>
                          <Chip 
                            label="대기중" 
                            size="small" 
                            color="warning"
                          />
                        </TableCell>
                        <TableCell align="center">
                          <IconButton color="success" size="small">
                            <CheckCircle />
                          </IconButton>
                          <IconButton color="error" size="small">
                            <Cancel />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        )}

        {tabValue === 2 && (
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                최근 주문
              </Typography>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>주문번호</TableCell>
                      <TableCell>상품</TableCell>
                      <TableCell>수량</TableCell>
                      <TableCell>금액</TableCell>
                      <TableCell>주문일</TableCell>
                      <TableCell>상태</TableCell>
                      <TableCell align="center">액션</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recentOrders.map((order) => (
                      <TableRow key={order.id}>
                        <TableCell>{order.id}</TableCell>
                        <TableCell>{order.product}</TableCell>
                        <TableCell>{order.quantity}</TableCell>
                        <TableCell>₩{order.total.toLocaleString()}</TableCell>
                        <TableCell>{order.orderDate}</TableCell>
                        <TableCell>
                          <Chip 
                            label={
                              order.status === 'PENDING' ? '대기중' :
                              order.status === 'SHIPPED' ? '배송중' : '배송완료'
                            }
                            size="small"
                            color={
                              order.status === 'PENDING' ? 'warning' :
                              order.status === 'SHIPPED' ? 'info' : 'success'
                            }
                          />
                        </TableCell>
                        <TableCell align="center">
                          <IconButton size="small">
                            <Edit />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        )}

        {tabValue === 3 && (
          <Box sx={{ display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ flex: '1 1 100%' }}>
              <Card>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                      재고 현황
                    </Typography>
                    <Button 
                      startIcon={<Add />}
                      variant="contained"
                      sx={{ 
                        backgroundColor: '#b8191c',
                        '&:hover': { backgroundColor: '#a01018' }
                      }}
                    >
                      재고 주문
                    </Button>
                  </Box>
                  <TableContainer>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>품목</TableCell>
                          <TableCell>현재 재고</TableCell>
                          <TableCell>최소 재고</TableCell>
                          <TableCell>상태</TableCell>
                          <TableCell align="center">액션</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {inventory.map((item, index) => (
                          <TableRow key={index}>
                            <TableCell>{item.item}</TableCell>
                            <TableCell>{item.current}</TableCell>
                            <TableCell>{item.minimum}</TableCell>
                            <TableCell>
                              <Chip 
                                label={item.status === 'GOOD' ? '양호' : '부족'}
                                size="small"
                                color={item.status === 'GOOD' ? 'success' : 'error'}
                              />
                            </TableCell>
                            <TableCell align="center">
                              <Button 
                                size="small" 
                                variant="outlined"
                                sx={{ 
                                  borderColor: item.status === 'LOW' ? '#ff9800' : '#ddd',
                                  color: item.status === 'LOW' ? '#ff9800' : '#666'
                                }}
                              >
                                주문
                              </Button>
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

        {tabValue === 4 && (
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  이번 달 대회 일정
                </Typography>
                <Button 
                  startIcon={<CalendarToday />}
                  variant="contained"
                  sx={{ 
                    backgroundColor: '#b8191c',
                    '&:hover': { backgroundColor: '#a01018' }
                  }}
                >
                  대회 생성
                </Button>
              </Box>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>날짜</TableCell>
                      <TableCell>대회명</TableCell>
                      <TableCell>형식</TableCell>
                      <TableCell>최대 인원</TableCell>
                      <TableCell>등록 인원</TableCell>
                      <TableCell align="center">관리</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {monthlyTournaments.map((tournament, index) => (
                      <TableRow key={index}>
                        <TableCell>{tournament.date}</TableCell>
                        <TableCell>{tournament.name}</TableCell>
                        <TableCell>{tournament.type}</TableCell>
                        <TableCell>{tournament.maxPlayers}</TableCell>
                        <TableCell>
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            {tournament.registered}/{tournament.maxPlayers}
                            {tournament.registered === tournament.maxPlayers && (
                              <Chip label="마감" size="small" color="error" />
                            )}
                          </Box>
                        </TableCell>
                        <TableCell align="center">
                          <IconButton size="small">
                            <Edit />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        )}
      </Container>
    </Box>
  );
};

export default StoreOwnerLoungePage;