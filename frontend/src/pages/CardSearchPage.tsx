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
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Collapse,
  Paper,
  Pagination,
  CircularProgress,
  Alert
} from '@mui/material';
import {
  Search,
  FilterList,
  ExpandMore,
  ExpandLess
} from '@mui/icons-material';
import Navigation from '../components/Navigation';
import { Card as CardType, cardApi } from '../services/api';

const CardSearchPage: React.FC = () => {
  const location = useLocation();
  const [searchQuery, setSearchQuery] = useState('');
  const [filtersOpen, setFiltersOpen] = useState(false);
  const [rarity, setRarity] = useState('all');
  const [color, setColor] = useState('all');
  const [cost, setCost] = useState('all');
  
  // API 상태 관리
  const [cards, setCards] = useState<CardType[]>([]);
  const [filteredCards, setFilteredCards] = useState<CardType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 카드 데이터 로드
  useEffect(() => {
    const loadCards = async () => {
      try {
        setLoading(true);
        setError(null);
        const cardsData = await cardApi.getAllCards();
        setCards(cardsData);
        setFilteredCards(cardsData);
      } catch (err) {
        setError('카드 데이터를 불러오는 중 오류가 발생했습니다.');
        console.error('Error loading cards:', err);
      } finally {
        setLoading(false);
      }
    };

    loadCards();
  }, []);

  // URL 파라미터에서 검색 조건을 가져와 상태 설정
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const query = params.get('query') || '';
    const rarityParam = params.get('rarity') || 'all';
    const colorParam = params.get('color') || 'all';
    
    setSearchQuery(query);
    setRarity(rarityParam);
    setColor(colorParam);
    
    if (query || rarityParam !== 'all' || colorParam !== 'all') {
      setFiltersOpen(true);
    }
  }, [location.search]);

  // 필터링 로직
  useEffect(() => {
    let filtered = cards;
    
    // 검색어 필터링
    if (searchQuery.trim()) {
      filtered = filtered.filter(card => 
        card.cardName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (card.description && card.description.toLowerCase().includes(searchQuery.toLowerCase()))
      );
    }
    
    // 희귀도 필터링
    if (rarity !== 'all') {
      filtered = filtered.filter(card => card.rarity === rarity);
    }
    
    // 색상 필터링
    if (color !== 'all') {
      filtered = filtered.filter(card => card.cardColor === color);
    }

    // 마나 비용 필터링
    if (cost !== 'all') {
      filtered = filtered.filter(card => {
        if (!card.cost) return false;
        switch (cost) {
          case '0-2': return card.cost <= 2;
          case '3-4': return card.cost >= 3 && card.cost <= 4;
          case '5-6': return card.cost >= 5 && card.cost <= 6;
          case '7+': return card.cost >= 7;
          default: return true;
        }
      });
    }
    
    setFilteredCards(filtered);
  }, [cards, searchQuery, rarity, color, cost]);

  const getRarityColor = (rarity: string) => {
    switch (rarity) {
      case 'COMMON': return '#9e9e9e';
      case 'RARE': return '#2196f3';
      case 'SUPER_RARE': return '#9c27b0';
      case 'ULTRA_RARE': return '#e91e63';
      case 'SECRET_RARE': return '#3f51b5';
      case 'LEGENDARY': return '#ff9800';
      default: return '#9e9e9e';
    }
  };

  const getRarityLabel = (rarity: string) => {
    switch (rarity) {
      case 'COMMON': return '일반';
      case 'RARE': return '희귀';
      case 'SUPER_RARE': return '슈퍼 희귀';
      case 'ULTRA_RARE': return '울트라 희귀';
      case 'SECRET_RARE': return '시크릿 희귀';
      case 'LEGENDARY': return '전설';
      default: return '일반';
    }
  };

  const getColorLabel = (color: string) => {
    switch (color) {
      case 'RED': return '빨강';
      case 'BLUE': return '파랑';
      case 'GREEN': return '초록';
      case 'YELLOW': return '노랑';
      case 'BLACK': return '검정';
      case 'COLORLESS': return '무색';
      default: return '무색';
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
            disabled={loading}
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
              disabled={loading}
              sx={{ borderColor: '#b8191c', color: '#b8191c' }}
            >
              고급 필터
            </Button>
          </Box>

          <Collapse in={filtersOpen}>
            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
              <Box sx={{ flex: '1 1 250px', minWidth: '250px' }}>
                <FormControl fullWidth>
                  <InputLabel>희귀도</InputLabel>
                  <Select value={rarity} onChange={(e) => setRarity(e.target.value)} label="희귀도" disabled={loading}>
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="COMMON">일반</MenuItem>
                    <MenuItem value="RARE">희귀</MenuItem>
                    <MenuItem value="SUPER_RARE">슈퍼 희귀</MenuItem>
                    <MenuItem value="ULTRA_RARE">울트라 희귀</MenuItem>
                    <MenuItem value="SECRET_RARE">시크릿 희귀</MenuItem>
                    <MenuItem value="LEGENDARY">전설</MenuItem>
                  </Select>
                </FormControl>
              </Box>
              
              <Box sx={{ flex: '1 1 250px', minWidth: '250px' }}>
                <FormControl fullWidth>
                  <InputLabel>색상</InputLabel>
                  <Select value={color} onChange={(e) => setColor(e.target.value)} label="색상" disabled={loading}>
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="RED">빨강</MenuItem>
                    <MenuItem value="BLUE">파랑</MenuItem>
                    <MenuItem value="GREEN">초록</MenuItem>
                    <MenuItem value="YELLOW">노랑</MenuItem>
                    <MenuItem value="BLACK">검정</MenuItem>
                    <MenuItem value="COLORLESS">무색</MenuItem>
                  </Select>
                </FormControl>
              </Box>
              
              <Box sx={{ flex: '1 1 250px', minWidth: '250px' }}>
                <FormControl fullWidth>
                  <InputLabel>마나 비용</InputLabel>
                  <Select value={cost} onChange={(e) => setCost(e.target.value)} label="마나 비용" disabled={loading}>
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

        {/* 에러 메시지 */}
        {error && (
          <Alert severity="error" sx={{ mb: 4 }}>
            {error}
          </Alert>
        )}

        {/* 로딩 상태 */}
        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mb: 4 }}>
            <CircularProgress />
          </Box>
        )}

        {/* 검색 결과 */}
        {!loading && (
          <Box sx={{ mb: 4 }}>
            <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3, color: '#333' }}>
              검색 결과 ({filteredCards.length}장)
            </Typography>
            {filteredCards.length === 0 ? (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <Typography variant="h6" color="text.secondary">
                  검색 결과가 없습니다.
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  검색 조건을 변경해보세요.
                </Typography>
              </Box>
            ) : (
              <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
                {filteredCards.map((card) => (
                  <Box key={card.cardId} sx={{ flex: '1 1 280px', minWidth: '280px' }}>
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
                        image={card.cardImg || 'https://via.placeholder.com/250x350/cccccc/ffffff?text=' + encodeURIComponent(card.cardName)}
                        alt={card.cardName}
                      />
                      <CardContent>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
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
                            label={getColorLabel(card.cardColor)}
                            size="small"
                            variant="outlined"
                          />
                        </Box>
                        
                        <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                          {card.cardName}
                        </Typography>
                        
                        {card.cost !== undefined && (
                          <Box sx={{ mb: 1 }}>
                            <Chip label={`비용: ${card.cost}`} size="small" color="primary" />
                          </Box>
                        )}
                        
                        <Typography variant="body2" color="text.secondary" sx={{ height: '40px', overflow: 'hidden' }}>
                          {card.description || '카드 설명이 없습니다.'}
                        </Typography>
                        
                        {card.cardNumber && (
                          <Typography variant="caption" color="text.disabled" sx={{ mt: 1, display: 'block' }}>
                            카드 번호: {card.cardNumber}
                          </Typography>
                        )}
                      </CardContent>
                    </Card>
                  </Box>
                ))}
              </Box>
            )}
          </Box>
        )}

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