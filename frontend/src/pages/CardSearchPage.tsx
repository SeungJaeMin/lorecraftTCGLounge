import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Button,
  Card,
  CardContent,
  CardMedia,
  Grid,
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  IconButton,
  Collapse,
  Paper,
  Pagination
} from '@mui/material';
import {
  Search,
  FilterList,
  ExpandMore,
  ExpandLess,
  Star,
  LocalFireDepartment,
  Shield,
  Bolt
} from '@mui/icons-material';
import Navigation from '../components/Navigation';

// 샘플 카드 데이터
const cards = [
  {
    id: 1,
    name: '화염 드래곤',
    type: 'UNIT',
    cost: 8,
    attack: 8,
    defense: 6,
    color: 'RED',
    rarity: 'LEGENDARY',
    image: 'https://via.placeholder.com/250x350/ff6b6b/ffffff?text=화염+드래곤',
    description: '강력한 화염 공격으로 적을 소멸시키는 고대 드래곤'
  },
  {
    id: 2,
    name: '빙결의 마법사',
    type: 'UNIT',
    cost: 5,
    attack: 3,
    defense: 5,
    color: 'BLUE',
    rarity: 'RARE',
    image: 'https://via.placeholder.com/250x350/4facfe/ffffff?text=빙결+마법사',
    description: '적을 얼음으로 묶어 움직임을 봉쇄하는 마법사'
  },
  {
    id: 3,
    name: '번개 폭풍',
    type: 'SPELL',
    cost: 3,
    color: 'YELLOW',
    rarity: 'COMMON',
    image: 'https://via.placeholder.com/250x350/f4c87a/ffffff?text=번개+폭풍',
    description: '모든 적에게 3 데미지를 준다'
  },
  {
    id: 4,
    name: '치유의 성수',
    type: 'SPELL',
    cost: 2,
    color: 'WHITE',
    rarity: 'COMMON',
    image: 'https://via.placeholder.com/250x350/e8f5e8/333333?text=치유+성수',
    description: '아군 유닛 하나의 체력을 5 회복한다'
  },
  {
    id: 5,
    name: '어둠의 검사',
    type: 'UNIT',
    cost: 4,
    attack: 5,
    defense: 3,
    color: 'BLACK',
    rarity: 'RARE',
    image: 'https://via.placeholder.com/250x350/333333/ffffff?text=어둠+검사',
    description: '적을 처치할 때마다 공격력이 1 증가한다'
  },
  {
    id: 6,
    name: '자연의 수호자',
    type: 'UNIT',
    cost: 6,
    attack: 4,
    defense: 8,
    color: 'GREEN',
    rarity: 'SUPER_RARE',
    image: 'https://via.placeholder.com/250x350/4caf50/ffffff?text=자연+수호자',
    description: '필드에 있는 동안 모든 아군의 방어력이 2 증가한다'
  }
];

const CardSearchPage: React.FC = () => {
  const location = useLocation();
  const [searchQuery, setSearchQuery] = useState('');
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [cardType, setCardType] = useState('all');
  const [rarity, setRarity] = useState('all');
  const [color, setColor] = useState('all');
  const [cost, setCost] = useState('all');
  const [filteredCards, setFilteredCards] = useState(cards);

  // URL 파라미터에서 검색 조건을 가져와 상태 설정
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const query = params.get('query') || '';
    const type = params.get('type') || 'all';
    const rarityParam = params.get('rarity') || 'all';
    const colorParam = params.get('color') || 'all';
    
    setSearchQuery(query);
    setCardType(type);
    setRarity(rarityParam);
    setColor(colorParam);
    
    if (query || type !== 'all' || rarityParam !== 'all' || colorParam !== 'all') {
      setFiltersOpen(true);
    }
  }, [location.search]);

  // 필터링 로직
  useEffect(() => {
    let filtered = cards;
    
    // 검색어 필터링
    if (searchQuery.trim()) {
      filtered = filtered.filter(card => 
        card.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        card.description.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }
    
    // 타입 필터링
    if (cardType !== 'all') {
      filtered = filtered.filter(card => card.type === cardType);
    }
    
    // 희귀도 필터링
    if (rarity !== 'all') {
      filtered = filtered.filter(card => card.rarity === rarity);
    }
    
    // 색상 필터링
    if (color !== 'all') {
      filtered = filtered.filter(card => card.color === color);
    }
    
    setFilteredCards(filtered);
  }, [searchQuery, cardType, rarity, color]);


  const getRarityColor = (rarity: string) => {
    switch (rarity) {
      case 'COMMON': return '#9e9e9e';
      case 'RARE': return '#2196f3';
      case 'SUPER_RARE': return '#9c27b0';
      case 'LEGENDARY': return '#ff9800';
      default: return '#9e9e9e';
    }
  };

  const getRarityLabel = (rarity: string) => {
    switch (rarity) {
      case 'COMMON': return '일반';
      case 'RARE': return '희귀';
      case 'SUPER_RARE': return '슈퍼 희귀';
      case 'LEGENDARY': return '전설';
      default: return '일반';
    }
  };

  const getColorLabel = (color: string) => {
    switch (color) {
      case 'RED': return '빨강';
      case 'BLUE': return '파랑';
      case 'GREEN': return '초록';
      case 'WHITE': return '하양';
      case 'BLACK': return '검정';
      case 'YELLOW': return '노랑';
      default: return '무색';
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'UNIT': return <Shield />;
      case 'SPELL': return <Bolt />;
      case 'ITEM': return <Star />;
      default: return <LocalFireDepartment />;
    }
  };

  return (
    <Box>
      <Navigation />

      {/* 메인 콘텐츠 */}
      <Container maxWidth="lg" sx={{ pt: 12, pb: 4 }}>
        {/* 페이지 헤더 */}
        <Box sx={{ mb: 4, textAlign: 'center' }}>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2, color: '#333' }}>
            카드 검색
          </Typography>
          <Typography variant="body1" color="text.secondary">
            원하는 카드를 찾아 덱을 구성해보세요
          </Typography>
        </Box>

        {/* 검색 및 필터 */}
        <Paper sx={{ p: 3, mb: 4 }}>
          <TextField
            fullWidth
            placeholder="카드명, 효과, 설명으로 검색..."
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
          
          <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
            <Button
              onClick={() => setFiltersOpen(!filtersOpen)}
              startIcon={<FilterList />}
              endIcon={filtersOpen ? <ExpandLess /> : <ExpandMore />}
              variant="outlined"
              sx={{ borderColor: '#b8191c', color: '#b8191c' }}
            >
              고급 필터
            </Button>
          </Box>

          <Collapse in={filtersOpen}>
            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
              <Box sx={{ flex: '1 1 250px', minWidth: '250px' }}>
                <FormControl fullWidth>
                  <InputLabel>카드 타입</InputLabel>
                  <Select value={cardType} onChange={(e) => setCardType(e.target.value)} label="카드 타입">
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="UNIT">유닛</MenuItem>
                    <MenuItem value="SPELL">스펠</MenuItem>
                    <MenuItem value="ITEM">아이템</MenuItem>
                  </Select>
                </FormControl>
              </Box>
              
              <Box sx={{ flex: '1 1 250px', minWidth: '250px' }}>
                <FormControl fullWidth>
                  <InputLabel>희귀도</InputLabel>
                  <Select value={rarity} onChange={(e) => setRarity(e.target.value)} label="희귀도">
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="COMMON">일반</MenuItem>
                    <MenuItem value="RARE">희귀</MenuItem>
                    <MenuItem value="SUPER_RARE">슈퍼 희귀</MenuItem>
                    <MenuItem value="LEGENDARY">전설</MenuItem>
                  </Select>
                </FormControl>
              </Box>
              
              <Box sx={{ flex: '1 1 250px', minWidth: '250px' }}>
                <FormControl fullWidth>
                  <InputLabel>색상</InputLabel>
                  <Select value={color} onChange={(e) => setColor(e.target.value)} label="색상">
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="RED">빨강</MenuItem>
                    <MenuItem value="BLUE">파랑</MenuItem>
                    <MenuItem value="GREEN">초록</MenuItem>
                    <MenuItem value="WHITE">하양</MenuItem>
                    <MenuItem value="BLACK">검정</MenuItem>
                    <MenuItem value="YELLOW">노랑</MenuItem>
                  </Select>
                </FormControl>
              </Box>
              
              <Box sx={{ flex: '1 1 250px', minWidth: '250px' }}>
                <FormControl fullWidth>
                  <InputLabel>마나 비용</InputLabel>
                  <Select value={cost} onChange={(e) => setCost(e.target.value)} label="마나 비용">
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="0-2">0-2</MenuItem>
                    <MenuItem value="3-4">3-4</MenuItem>
                    <MenuItem value="5-6">5-6</MenuItem>
                    <MenuItem value="7+">7+</MenuItem>
                  </Select>
                </FormControl>
              </Box>
            </Box>
          </Collapse>
        </Paper>

        {/* 검색 결과 */}
        <Box sx={{ mb: 4 }}>
          <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
            검색 결과 ({filteredCards.length}장)
          </Typography>
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {filteredCards.map((card) => (
              <Box key={card.id} sx={{ flex: '1 1 280px', minWidth: '280px' }}>
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
                    height="300"
                    image={card.image}
                    alt={card.name}
                  />
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                      <Box sx={{ mr: 1 }}>
                        {getTypeIcon(card.type)}
                      </Box>
                      <Chip 
                        label={getRarityLabel(card.rarity)}
                        size="small"
                        sx={{ 
                          backgroundColor: getRarityColor(card.rarity),
                          color: 'white',
                          mr: 1
                        }}
                      />
                      <Chip 
                        label={getColorLabel(card.color)}
                        size="small"
                        variant="outlined"
                      />
                    </Box>
                    
                    <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                      {card.name}
                    </Typography>
                    
                    {card.type === 'UNIT' && (
                      <Box sx={{ display: 'flex', gap: 1, mb: 1 }}>
                        <Chip label={`비용: ${card.cost}`} size="small" color="primary" />
                        <Chip label={`${card.attack}/${card.defense}`} size="small" color="secondary" />
                      </Box>
                    )}
                    
                    {card.type === 'SPELL' && (
                      <Box sx={{ mb: 1 }}>
                        <Chip label={`비용: ${card.cost}`} size="small" color="primary" />
                      </Box>
                    )}
                    
                    <Typography variant="body2" color="text.secondary" sx={{ height: '40px', overflow: 'hidden' }}>
                      {card.description}
                    </Typography>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        </Box>

        {/* 페이지네이션 */}
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Pagination 
            count={Math.ceil(filteredCards.length / 8)} 
            color="primary"
            size="large"
          />
        </Box>
      </Container>
    </Box>
  );
};

export default CardSearchPage;