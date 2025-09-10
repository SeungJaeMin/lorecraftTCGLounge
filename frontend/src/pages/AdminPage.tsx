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
  Switch,
  FormControlLabel,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  LinearProgress,
  Alert
} from '@mui/material';
import {
  Dashboard,
  People,
  Store,
  Event,
  Assessment,
  Settings,
  Security,
  Notifications,
  AdminPanelSettings,
  Logout,
  NotificationsActive,
  Mail,
  CheckCircle,
  Cancel,
  Edit,
  Delete,
  Add,
  Warning,
  TrendingUp,
  TrendingDown,
  AttachMoney,
  Groups,
  EmojiEvents,
  Block,
  Verified,
  ContentCopy,
  CloudUpload,
  Analytics,
  ReportProblem
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import ArticleManagement from '../components/admin/ArticleManagement';
import ArticleEditor from '../components/admin/ArticleEditor';
import TagManagement from '../components/admin/TagManagement';

const AdminPage: React.FC = () => {
  const navigate = useNavigate();
  const [tabValue, setTabValue] = React.useState(0);
  const [selectedUserType, setSelectedUserType] = React.useState('all');
  
  // 게시글 관리 상태
  const [showArticleEditor, setShowArticleEditor] = React.useState(false);
  const [selectedArticle, setSelectedArticle] = React.useState<any>(null);
  const [showTagManagement, setShowTagManagement] = React.useState(false);

  const handleLogout = () => {
    navigate('/');
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  // 시스템 통계
  const systemStats = {
    totalUsers: 12543,
    activeUsers: 8921,
    totalStores: 156,
    activeCompetitions: 42,
    monthlyRevenue: 125000000,
    systemHealth: 98.5,
    serverLoad: 45,
    dbConnections: 128
  };

  // 사용자 관리 데이터
  const users = [
    { 
      id: 1, 
      username: 'dragonmaster', 
      type: 'GAMER', 
      email: 'dragon@test.com', 
      status: 'ACTIVE',
      joinDate: '2024-01-15',
      lastLogin: '2024-08-20 14:30',
      violations: 0
    },
    { 
      id: 2, 
      username: 'cardshop123', 
      type: 'STORE_OWNER', 
      email: 'shop@test.com', 
      status: 'ACTIVE',
      joinDate: '2024-02-20',
      lastLogin: '2024-08-20 09:15',
      violations: 1
    },
    { 
      id: 3, 
      username: 'progamer', 
      type: 'GAMER', 
      email: 'pro@test.com', 
      status: 'SUSPENDED',
      joinDate: '2023-12-10',
      lastLogin: '2024-08-18 22:45',
      violations: 3
    },
    { 
      id: 4, 
      username: 'megastore', 
      type: 'STORE_OWNER', 
      email: 'mega@test.com', 
      status: 'PENDING',
      joinDate: '2024-08-19',
      lastLogin: '-',
      violations: 0
    }
  ];

  // 대회 관리 데이터
  const competitions = [
    {
      id: 1,
      name: '전국 챔피언십',
      organizer: 'ESTELA 본부',
      date: '2024-08-30',
      participants: 256,
      status: 'APPROVED',
      prizePool: '1000만원'
    },
    {
      id: 2,
      name: '서울 지역 리그',
      organizer: '강남 카드샵',
      date: '2024-08-25',
      participants: 64,
      status: 'PENDING',
      prizePool: '300만원'
    },
    {
      id: 3,
      name: '초보자 친선대회',
      organizer: '홍대 TCG 라운지',
      date: '2024-08-27',
      participants: 32,
      status: 'APPROVED',
      prizePool: '100만원'
    }
  ];

  // 점주 승인 대기
  const pendingStoreOwners = [
    {
      id: 1,
      storeName: '부산 TCG 마켓',
      ownerName: '김점주',
      location: '부산 해운대구',
      businessLicense: 'BUS-2024-001',
      requestDate: '2024-08-19'
    },
    {
      id: 2,
      storeName: '대전 카드 월드',
      ownerName: '이사장',
      location: '대전 중구',
      businessLicense: 'DAE-2024-002',
      requestDate: '2024-08-18'
    }
  ];

  // 시스템 알림
  const systemAlerts = [
    { id: 1, type: 'ERROR', message: '데이터베이스 연결 지연 감지', time: '10분 전', severity: 'HIGH' },
    { id: 2, type: 'WARNING', message: '서버 CPU 사용률 85% 초과', time: '30분 전', severity: 'MEDIUM' },
    { id: 3, type: 'INFO', message: '백업 완료', time: '1시간 전', severity: 'LOW' },
    { id: 4, type: 'ERROR', message: '결제 시스템 오류 보고', time: '2시간 전', severity: 'HIGH' }
  ];

  // TODO: 실제 게시글 관리 기능으로 대체됨 - 더미 데이터 제거

  return (
    <Box sx={{ bgcolor: '#f5f5f5', minHeight: '100vh' }}>
      {/* 네비게이션 바 */}
      <AppBar position="fixed" sx={{ backgroundColor: '#1a1a1a' }}>
        <Toolbar sx={{ minHeight: '80px !important', height: '80px' }}>
          <AdminPanelSettings sx={{ mr: 2, color: '#f4c87a' }} />
          <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#f4c87a' }}>
            ESTELA ⭐ 관리자 시스템
          </Typography>
          
          <Box sx={{ flexGrow: 1, display: 'flex', justifyContent: 'center', gap: 3 }}>
            <Button sx={{ color: 'white' }} startIcon={<Dashboard />}>대시보드</Button>
            <Button sx={{ color: 'white' }} startIcon={<People />}>사용자 관리</Button>
            <Button sx={{ color: 'white' }} startIcon={<Store />}>점주 관리</Button>
            <Button sx={{ color: 'white' }} startIcon={<Event />}>대회 관리</Button>
            <Button sx={{ color: 'white' }} startIcon={<Assessment />}>통계</Button>
            <Button sx={{ color: 'white' }} startIcon={<Settings />}>시스템 설정</Button>
          </Box>

          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            <Badge badgeContent={systemAlerts.filter(a => a.severity === 'HIGH').length} color="error">
              <NotificationsActive sx={{ color: 'white', cursor: 'pointer' }} />
            </Badge>
            <Badge badgeContent={3} color="primary">
              <Mail sx={{ color: 'white', cursor: 'pointer' }} />
            </Badge>
            <Button 
              startIcon={<Security />} 
              sx={{ color: 'white', borderColor: 'white' }}
              variant="outlined"
            >
              시스템 관리자
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
        {/* 시스템 상태 알림 */}
        {systemAlerts.filter(a => a.severity === 'HIGH').length > 0 && (
          <Alert severity="error" sx={{ mb: 3 }}>
            <strong>긴급:</strong> {systemAlerts.filter(a => a.severity === 'HIGH').length}개의 중요 시스템 알림이 있습니다.
          </Alert>
        )}

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
                      전체 사용자
                    </Typography>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', mt: 1 }}>
                      {systemStats.totalUsers.toLocaleString()}
                    </Typography>
                    <Chip 
                      icon={<TrendingUp />}
                      label="+8.3%" 
                      size="small"
                      sx={{ 
                        mt: 1,
                        bgcolor: 'rgba(255,255,255,0.2)', 
                        color: 'white'
                      }} 
                    />
                  </Box>
                  <Groups sx={{ fontSize: 40, opacity: 0.7 }} />
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
                      활성 대회
                    </Typography>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', mt: 1 }}>
                      {systemStats.activeCompetitions}
                    </Typography>
                    <Typography variant="caption" sx={{ opacity: 0.9 }}>
                      진행중인 대회
                    </Typography>
                  </Box>
                  <EmojiEvents sx={{ fontSize: 40, opacity: 0.7 }} />
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
                      월 매출
                    </Typography>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', mt: 1 }}>
                      ₩{(systemStats.monthlyRevenue / 1000000).toFixed(0)}M
                    </Typography>
                    <Chip 
                      icon={<TrendingUp />}
                      label="+15.2%" 
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
              background: systemStats.systemHealth > 95 ? 
                'linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%)' :
                'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
              color: 'white'
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Box>
                    <Typography variant="body2" sx={{ opacity: 0.9 }}>
                      시스템 상태
                    </Typography>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', mt: 1 }}>
                      {systemStats.systemHealth}%
                    </Typography>
                    <LinearProgress 
                      variant="determinate" 
                      value={systemStats.systemHealth} 
                      sx={{ 
                        mt: 1, 
                        height: 6, 
                        borderRadius: 3,
                        bgcolor: 'rgba(255,255,255,0.3)',
                        '& .MuiLinearProgress-bar': {
                          bgcolor: 'white'
                        }
                      }}
                    />
                  </Box>
                  <Analytics sx={{ fontSize: 40, opacity: 0.7 }} />
                </Box>
              </CardContent>
            </Card>
          </Box>
        </Box>

        {/* 탭 섹션 */}
        <Paper sx={{ mb: 3 }}>
          <Tabs value={tabValue} onChange={handleTabChange} sx={{ borderBottom: 1, borderColor: 'divider' }}>
            <Tab label="사용자 관리" />
            <Tab label="대회 승인" />
            <Tab label="점주 승인" />
            <Tab label="뉴스/공지 관리" />
            <Tab label="시스템 알림" />
          </Tabs>
        </Paper>

        {/* 탭 콘텐츠 */}
        {tabValue === 0 && (
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  사용자 목록
                </Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                  <FormControl size="small" sx={{ minWidth: 150 }}>
                    <InputLabel>사용자 유형</InputLabel>
                    <Select 
                      value={selectedUserType} 
                      onChange={(e) => setSelectedUserType(e.target.value)}
                      label="사용자 유형"
                    >
                      <MenuItem value="all">전체</MenuItem>
                      <MenuItem value="GAMER">게이머</MenuItem>
                      <MenuItem value="STORE_OWNER">점주</MenuItem>
                    </Select>
                  </FormControl>
                  <TextField 
                    size="small" 
                    placeholder="사용자 검색..." 
                    sx={{ width: 200 }}
                  />
                </Box>
              </Box>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>사용자명</TableCell>
                      <TableCell>유형</TableCell>
                      <TableCell>이메일</TableCell>
                      <TableCell>상태</TableCell>
                      <TableCell>가입일</TableCell>
                      <TableCell>마지막 로그인</TableCell>
                      <TableCell>위반</TableCell>
                      <TableCell align="center">액션</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {users.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell>{user.username}</TableCell>
                        <TableCell>
                          <Chip 
                            label={user.type === 'GAMER' ? '게이머' : '점주'} 
                            size="small"
                            color={user.type === 'GAMER' ? 'primary' : 'secondary'}
                          />
                        </TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>
                          <Chip 
                            label={
                              user.status === 'ACTIVE' ? '활성' :
                              user.status === 'SUSPENDED' ? '정지' : '대기'
                            }
                            size="small"
                            color={
                              user.status === 'ACTIVE' ? 'success' :
                              user.status === 'SUSPENDED' ? 'error' : 'warning'
                            }
                          />
                        </TableCell>
                        <TableCell>{user.joinDate}</TableCell>
                        <TableCell>{user.lastLogin}</TableCell>
                        <TableCell>
                          {user.violations > 0 && (
                            <Chip 
                              label={user.violations} 
                              size="small" 
                              color={user.violations > 2 ? 'error' : 'warning'}
                            />
                          )}
                        </TableCell>
                        <TableCell align="center">
                          <IconButton size="small" color="primary">
                            <Edit />
                          </IconButton>
                          {user.status === 'ACTIVE' ? (
                            <IconButton size="small" color="warning">
                              <Block />
                            </IconButton>
                          ) : (
                            <IconButton size="small" color="success">
                              <CheckCircle />
                            </IconButton>
                          )}
                          <IconButton size="small" color="error">
                            <Delete />
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

        {tabValue === 1 && (
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                대회 승인 관리
              </Typography>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>대회명</TableCell>
                      <TableCell>주최자</TableCell>
                      <TableCell>날짜</TableCell>
                      <TableCell>참가자</TableCell>
                      <TableCell>상금</TableCell>
                      <TableCell>상태</TableCell>
                      <TableCell align="center">액션</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {competitions.map((comp) => (
                      <TableRow key={comp.id}>
                        <TableCell>{comp.name}</TableCell>
                        <TableCell>{comp.organizer}</TableCell>
                        <TableCell>{comp.date}</TableCell>
                        <TableCell>{comp.participants}명</TableCell>
                        <TableCell sx={{ color: '#f4c87a', fontWeight: 'bold' }}>
                          {comp.prizePool}
                        </TableCell>
                        <TableCell>
                          <Chip 
                            label={comp.status === 'APPROVED' ? '승인됨' : '대기중'}
                            size="small"
                            color={comp.status === 'APPROVED' ? 'success' : 'warning'}
                          />
                        </TableCell>
                        <TableCell align="center">
                          {comp.status === 'PENDING' && (
                            <>
                              <IconButton size="small" color="success">
                                <CheckCircle />
                              </IconButton>
                              <IconButton size="small" color="error">
                                <Cancel />
                              </IconButton>
                            </>
                          )}
                          <IconButton size="small" color="primary">
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

        {tabValue === 2 && (
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                점주 승인 대기 목록
              </Typography>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>매장명</TableCell>
                      <TableCell>점주명</TableCell>
                      <TableCell>위치</TableCell>
                      <TableCell>사업자 번호</TableCell>
                      <TableCell>신청일</TableCell>
                      <TableCell align="center">액션</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {pendingStoreOwners.map((store) => (
                      <TableRow key={store.id}>
                        <TableCell>{store.storeName}</TableCell>
                        <TableCell>{store.ownerName}</TableCell>
                        <TableCell>{store.location}</TableCell>
                        <TableCell>{store.businessLicense}</TableCell>
                        <TableCell>{store.requestDate}</TableCell>
                        <TableCell align="center">
                          <Button 
                            size="small" 
                            variant="contained"
                            startIcon={<Verified />}
                            sx={{ 
                              mr: 1,
                              backgroundColor: '#4caf50',
                              '&:hover': { backgroundColor: '#45a049' }
                            }}
                          >
                            승인
                          </Button>
                          <Button 
                            size="small" 
                            variant="outlined"
                            startIcon={<Cancel />}
                            color="error"
                          >
                            거절
                          </Button>
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
          <Box>
            {!showArticleEditor && !showTagManagement && (
              <ArticleManagement
                onCreateNew={() => {
                  setSelectedArticle(null);
                  setShowArticleEditor(true);
                }}
                onEdit={(article) => {
                  setSelectedArticle(article);
                  setShowArticleEditor(true);
                }}
              />
            )}

            {showTagManagement && (
              <TagManagement
                onClose={() => setShowTagManagement(false)}
              />
            )}

            <Box sx={{ mt: 2, display: 'flex', gap: 2, justifyContent: 'center' }}>
              <Button
                variant={showTagManagement ? 'contained' : 'outlined'}
                onClick={() => {
                  setShowTagManagement(!showTagManagement);
                  setShowArticleEditor(false);
                }}
                startIcon={<Settings />}
              >
                태그 관리
              </Button>
            </Box>

            <ArticleEditor
              open={showArticleEditor}
              article={selectedArticle}
              onClose={() => {
                setShowArticleEditor(false);
                setSelectedArticle(null);
              }}
              onSave={(article) => {
                console.log('Article saved:', article);
              }}
            />
          </Box>
        )}

        {tabValue === 4 && (
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            {systemAlerts.map((alert) => (
              <Box key={alert.id} sx={{ flex: '1 1 100%' }}>
                <Alert 
                  severity={
                    alert.type === 'ERROR' ? 'error' :
                    alert.type === 'WARNING' ? 'warning' : 'info'
                  }
                  action={
                    <Button color="inherit" size="small">
                      처리
                    </Button>
                  }
                >
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                    <Box>
                      <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                        {alert.message}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {alert.time}
                      </Typography>
                    </Box>
                    <Chip 
                      label={
                        alert.severity === 'HIGH' ? '긴급' :
                        alert.severity === 'MEDIUM' ? '중요' : '정보'
                      }
                      size="small"
                      color={
                        alert.severity === 'HIGH' ? 'error' :
                        alert.severity === 'MEDIUM' ? 'warning' : 'info'
                      }
                    />
                  </Box>
                </Alert>
              </Box>
            ))}
          </Box>
        )}

        {/* 시스템 모니터링 섹션 */}
        <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap', mt: 3 }}>
          <Box sx={{ flex: '1 1 400px', minWidth: '400px' }}>
            <Card>
              <CardContent>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                  서버 상태
                </Typography>
                <Box sx={{ mb: 2 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">CPU 사용률</Typography>
                    <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                      {systemStats.serverLoad}%
                    </Typography>
                  </Box>
                  <LinearProgress 
                    variant="determinate" 
                    value={systemStats.serverLoad} 
                    sx={{ 
                      height: 8, 
                      borderRadius: 4,
                      bgcolor: '#e0e0e0',
                      '& .MuiLinearProgress-bar': {
                        bgcolor: systemStats.serverLoad > 80 ? '#f44336' : '#4caf50'
                      }
                    }}
                  />
                </Box>
                <Box sx={{ mb: 2 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">메모리 사용률</Typography>
                    <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                      62%
                    </Typography>
                  </Box>
                  <LinearProgress 
                    variant="determinate" 
                    value={62} 
                    sx={{ 
                      height: 8, 
                      borderRadius: 4,
                      bgcolor: '#e0e0e0',
                      '& .MuiLinearProgress-bar': {
                        bgcolor: '#2196f3'
                      }
                    }}
                  />
                </Box>
                <Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2">DB 연결</Typography>
                    <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                      {systemStats.dbConnections}/200
                    </Typography>
                  </Box>
                  <LinearProgress 
                    variant="determinate" 
                    value={(systemStats.dbConnections / 200) * 100} 
                    sx={{ 
                      height: 8, 
                      borderRadius: 4,
                      bgcolor: '#e0e0e0',
                      '& .MuiLinearProgress-bar': {
                        bgcolor: '#ff9800'
                      }
                    }}
                  />
                </Box>
              </CardContent>
            </Card>
          </Box>

          <Box sx={{ flex: '1 1 400px', minWidth: '400px' }}>
            <Card>
              <CardContent>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                  빠른 설정
                </Typography>
                <List>
                  <ListItem>
                    <ListItemText 
                      primary="유지보수 모드"
                      secondary="사이트를 유지보수 모드로 전환"
                    />
                    <Switch />
                  </ListItem>
                  <ListItem>
                    <ListItemText 
                      primary="신규 가입 허용"
                      secondary="신규 사용자 가입 허용/차단"
                    />
                    <Switch defaultChecked />
                  </ListItem>
                  <ListItem>
                    <ListItemText 
                      primary="대회 생성 허용"
                      secondary="점주의 대회 생성 권한"
                    />
                    <Switch defaultChecked />
                  </ListItem>
                  <ListItem>
                    <ListItemText 
                      primary="디버그 모드"
                      secondary="시스템 디버그 로그 활성화"
                    />
                    <Switch />
                  </ListItem>
                </List>
              </CardContent>
            </Card>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default AdminPage;