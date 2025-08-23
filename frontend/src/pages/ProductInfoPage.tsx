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
  TextField,
  InputAdornment,
  Tabs,
  Tab,
  IconButton,
  Rating,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Avatar
} from '@mui/material';
import {
  Search,
  ShoppingCart,
  Star,
  ExpandMore,
  Download,
  VideoLibrary,
  Image as ImageIcon,
  Launch
} from '@mui/icons-material';
import Navigation from '../components/Navigation';

const ProductInfoPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
  };

  // 샘플 제품 데이터
  const products = [
    {
      id: 1,
      name: '드래곤의 귀환 부스터팩',
      type: 'BOOSTER',
      price: 4500,
      image: 'https://via.placeholder.com/300x400/667eea/ffffff?text=드래곤의+귀환',
      description: '강력한 드래곤 카드들이 포함된 새로운 확장팩입니다.',
      releaseDate: '2024-08-30',
      cardsPerPack: 15,
      rating: 4.8,
      reviews: 234,
      inStock: true,
      featured: true
    },
    {
      id: 2,
      name: '스타터 덱 - 화염의 전사',
      type: 'STARTER',
      price: 15000,
      image: 'https://via.placeholder.com/300x400/f093fb/ffffff?text=화염의+전사',
      description: '초보자를 위한 완성된 덱입니다. 바로 게임을 시작할 수 있습니다.',
      releaseDate: '2024-08-01',
      cardsPerPack: 60,
      rating: 4.6,
      reviews: 156,
      inStock: true,
      featured: true
    },
    {
      id: 3,
      name: '스타터 덱 - 바람의 마법사',
      type: 'STARTER',
      price: 15000,
      image: 'https://via.placeholder.com/300x400/4facfe/ffffff?text=바람의+마법사',
      description: '마법 카드 중심의 전략적 플레이가 가능한 스타터 덱입니다.',
      releaseDate: '2024-08-01',
      cardsPerPack: 60,
      rating: 4.7,
      reviews: 189,
      inStock: true,
      featured: false
    },
    {
      id: 4,
      name: '콜렉터 에디션 박스',
      type: 'COLLECTOR',
      price: 120000,
      image: 'https://via.placeholder.com/300x400/fa709a/ffffff?text=콜렉터+에디션',
      description: '한정판 카드와 특별 아트워크가 포함된 프리미엄 제품입니다.',
      releaseDate: '2024-09-15',
      cardsPerPack: 200,
      rating: 4.9,
      reviews: 67,
      inStock: false,
      featured: true
    },
    {
      id: 5,
      name: '프리미엄 슬리브',
      type: 'ACCESSORY',
      price: 8000,
      image: 'https://via.placeholder.com/300x400/84fab0/ffffff?text=프리미엄+슬리브',
      description: '카드를 보호하는 고품질 슬리브입니다.',
      releaseDate: '2024-07-15',
      cardsPerPack: 100,
      rating: 4.4,
      reviews: 298,
      inStock: true,
      featured: false
    },
    {
      id: 6,
      name: '플레이매트 - 드래곤 테마',
      type: 'ACCESSORY',
      price: 25000,
      image: 'https://via.placeholder.com/300x400/764ba2/ffffff?text=플레이매트',
      description: '게임 플레이를 위한 고품질 플레이매트입니다.',
      releaseDate: '2024-07-01',
      cardsPerPack: 1,
      rating: 4.3,
      reviews: 145,
      inStock: true,
      featured: false
    }
  ];

  const categories = [
    { key: 'ALL', label: '전체' },
    { key: 'BOOSTER', label: '부스터팩' },
    { key: 'STARTER', label: '스타터덱' },
    { key: 'COLLECTOR', label: '콜렉터' },
    { key: 'ACCESSORY', label: '액세서리' }
  ];

  const getProductTypeLabel = (type: string) => {
    switch (type) {
      case 'BOOSTER': return '부스터팩';
      case 'STARTER': return '스타터덱';
      case 'COLLECTOR': return '콜렉터 에디션';
      case 'ACCESSORY': return '액세서리';
      default: return '기타';
    }
  };

  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         product.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = tabValue === 0 || 
                           categories[tabValue].key === product.type;
    return matchesSearch && matchesCategory;
  });

  const featuredProducts = products.filter(product => product.featured);

  return (
    <Box>
      <Navigation />

      {/* 메인 콘텐츠 */}
      <Container maxWidth="lg" sx={{ pt: 12, pb: 4 }}>
        {/* 페이지 헤더 */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2, color: '#333' }}>
            제품정보
          </Typography>
          <Typography variant="body1" color="text.secondary">
            ESTELA TCG의 모든 제품을 확인하고 구매하세요
          </Typography>
        </Box>

        {/* 검색 및 필터 */}
        <Box sx={{ mb: 4 }}>
          <TextField
            fullWidth
            placeholder="제품 검색..."
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
                label={category.label}
              />
            ))}
          </Tabs>
        </Box>

        {/* 추천 제품 섹션 */}
        {tabValue === 0 && featuredProducts.length > 0 && (
          <Box sx={{ mb: 6 }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
              추천 제품
            </Typography>
            <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
              {featuredProducts.map((product) => (
                <Box key={product.id} sx={{ flex: '1 1 350px', minWidth: '350px' }}>
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
                      height="240"
                      image={product.image}
                      alt={product.name}
                    />
                    <CardContent>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <Chip 
                          label={getProductTypeLabel(product.type)}
                          size="small"
                          color="primary"
                          sx={{ mr: 1 }}
                        />
                        <Chip 
                          label="추천"
                          size="small"
                          color="error"
                        />
                      </Box>
                      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                        {product.name}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                        {product.description}
                      </Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <Rating value={product.rating} readOnly precision={0.1} size="small" />
                        <Typography variant="caption" sx={{ ml: 1 }}>
                          ({product.reviews})
                        </Typography>
                      </Box>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#b8191c' }}>
                          ₩{product.price.toLocaleString()}
                        </Typography>
                        <Button 
                          variant="contained"
                          startIcon={<ShoppingCart />}
                          size="small"
                          disabled={!product.inStock}
                          sx={{ 
                            backgroundColor: product.inStock ? '#b8191c' : '#ccc',
                            '&:hover': { backgroundColor: product.inStock ? '#a01018' : '#ccc' }
                          }}
                        >
                          {product.inStock ? '구매' : '품절'}
                        </Button>
                      </Box>
                    </CardContent>
                  </Card>
                </Box>
              ))}
            </Box>
          </Box>
        )}

        {/* 제품 목록 */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
            {tabValue === 0 ? '전체 제품' : categories[tabValue].label}
          </Typography>
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {filteredProducts.map((product) => (
              <Box key={product.id} sx={{ flex: '1 1 280px', minWidth: '280px' }}>
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
                    height="200"
                    image={product.image}
                    alt={product.name}
                  />
                  <CardContent sx={{ display: 'flex', flexDirection: 'column', height: '220px' }}>
                    <Chip 
                      label={getProductTypeLabel(product.type)}
                      size="small"
                      color="primary"
                      sx={{ alignSelf: 'flex-start', mb: 1 }}
                    />
                    <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1, fontSize: '1rem' }}>
                      {product.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 2, flexGrow: 1 }}>
                      {product.description}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                      <Rating value={product.rating} readOnly precision={0.1} size="small" />
                      <Typography variant="caption" sx={{ ml: 1 }}>
                        ({product.reviews})
                      </Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#b8191c' }}>
                        ₩{product.price.toLocaleString()}
                      </Typography>
                      <Button 
                        variant="contained"
                        size="small"
                        disabled={!product.inStock}
                        sx={{ 
                          backgroundColor: product.inStock ? '#b8191c' : '#ccc',
                          '&:hover': { backgroundColor: product.inStock ? '#a01018' : '#ccc' }
                        }}
                      >
                        {product.inStock ? '구매' : '품절'}
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        </Box>

        {/* 추가 정보 섹션 */}
        <Box sx={{ mt: 6 }}>
          <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
            구매 가이드
          </Typography>
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            <Box sx={{ flex: '1 1 400px', minWidth: '400px' }}>
              <Accordion>
                <AccordionSummary expandIcon={<ExpandMore />}>
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    제품 구매 방법
                  </Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <List>
                    <ListItem>
                      <ListItemText primary="1. 원하는 제품을 선택하세요" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="2. 구매 버튼을 클릭하세요" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="3. 결제 정보를 입력하세요" />
                    </ListItem>
                    <ListItem>
                      <ListItemText primary="4. 주문 완료 후 배송을 기다리세요" />
                    </ListItem>
                  </List>
                </AccordionDetails>
              </Accordion>

              <Accordion>
                <AccordionSummary expandIcon={<ExpandMore />}>
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    배송 정보
                  </Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography variant="body2" paragraph>
                    • 배송비: 3만원 이상 구매 시 무료배송<br/>
                    • 배송기간: 주문 후 2-3일 (영업일 기준)<br/>
                    • 배송지역: 전국 (도서산간 지역 추가 배송비 발생)
                  </Typography>
                </AccordionDetails>
              </Accordion>

              <Accordion>
                <AccordionSummary expandIcon={<ExpandMore />}>
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    교환 및 환불
                  </Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography variant="body2" paragraph>
                    • 교환/환불 기간: 상품 수령 후 7일 이내<br/>
                    • 교환/환불 조건: 상품 미개봉 시에만 가능<br/>
                    • 단순 변심에 의한 반품 시 배송비 고객 부담
                  </Typography>
                </AccordionDetails>
              </Accordion>
            </Box>

            <Box sx={{ flex: '1 1 400px', minWidth: '400px' }}>
              <Card sx={{ p: 3, backgroundColor: '#f8f9fa' }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                  다운로드 자료
                </Typography>
                <List>
                  <ListItem sx={{ borderRadius: 2, mb: 1, backgroundColor: 'white', p: 0 }}>
                    <ListItemButton sx={{ p: 2 }}>
                      <Avatar sx={{ mr: 2, backgroundColor: '#b8191c' }}>
                        <Download />
                      </Avatar>
                      <ListItemText 
                        primary="제품 카탈로그"
                        secondary="최신 제품 정보 PDF"
                      />
                      <IconButton>
                        <Launch />
                      </IconButton>
                    </ListItemButton>
                  </ListItem>
                  <ListItem sx={{ borderRadius: 2, mb: 1, backgroundColor: 'white', p: 0 }}>
                    <ListItemButton sx={{ p: 2 }}>
                      <Avatar sx={{ mr: 2, backgroundColor: '#4caf50' }}>
                        <VideoLibrary />
                      </Avatar>
                      <ListItemText 
                        primary="언박싱 영상"
                        secondary="제품 개봉 가이드"
                      />
                      <IconButton>
                        <Launch />
                      </IconButton>
                    </ListItemButton>
                  </ListItem>
                  <ListItem sx={{ borderRadius: 2, backgroundColor: 'white', p: 0 }}>
                    <ListItemButton sx={{ p: 2 }}>
                      <Avatar sx={{ mr: 2, backgroundColor: '#ff9800' }}>
                        <ImageIcon />
                      </Avatar>
                      <ListItemText 
                        primary="고화질 이미지"
                        secondary="제품 상세 이미지 팩"
                      />
                      <IconButton>
                        <Launch />
                      </IconButton>
                    </ListItemButton>
                  </ListItem>
                </List>
              </Card>
            </Box>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default ProductInfoPage;