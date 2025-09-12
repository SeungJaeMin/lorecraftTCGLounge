import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
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
import LoginModal from '../components/LoginModal';
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
  const [loginModalOpen, setLoginModalOpen] = useState(false);

  // 카드 데이터 로드
  useEffect(() => {
    const loadCards = async () => {
      try {
        setLoading(true);
        setError(null);
        
        const response = await cardApi.getAllCards();
        setCards(response);
        setFilteredCards(response);
      } catch (err) {
        console.error('Error loading cards:', err);
        setError('카드 데이터를 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    loadCards();
  }, []);

  // URL에서 검색어 추출
  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const query = urlParams.get('query');
    if (query) {
      setSearchQuery(query);
    }
  }, [location.search]);

  // 필터링 로직
  useEffect(() => {
    let filtered = cards.filter(card => {
      const matchesSearch = searchQuery === '' || 
        card.cardName.toLowerCase().includes(searchQuery.toLowerCase());
      
      const matchesRarity = rarity === 'all' || card.rarity === rarity;
      const matchesColor = color === 'all' || card.cardColor === color;
      const matchesCost = cost === 'all' || 
        (cost === '0-3' && card.cost !== undefined && card.cost <= 3) ||
        (cost === '4-6' && card.cost !== undefined && card.cost >= 4 && card.cost <= 6) ||
        (cost === '7+' && card.cost !== undefined && card.cost >= 7);

      return matchesSearch && matchesRarity && matchesColor && matchesCost;
    });

    setFilteredCards(filtered);
  }, [cards, searchQuery, rarity, color, cost]);

  const getRarityLabel = (rarity: string) => {
    const rarityMap: { [key: string]: string } = {
      'COMMON': '커먼',
      'RARE': '레어',
      'SUPER_RARE': '슈퍼레어',
      'ULTRA_RARE': '울트라레어',
      'SECRET_RARE': '시크릿레어',
      'LEGENDARY': '레전더리'
    };
    return rarityMap[rarity] || rarity;
  };

  const getRarityColor = (rarity: string) => {
    const colorMap: { [key: string]: string } = {
      'COMMON': '#9e9e9e',
      'RARE': '#2196f3',
      'SUPER_RARE': '#9c27b0',
      'ULTRA_RARE': '#ff9800',
      'SECRET_RARE': '#f44336',
      'LEGENDARY': '#ffd700'
    };
    return colorMap[rarity] || '#9e9e9e';
  };

  const getColorLabel = (color: string) => {
    const colorMap: { [key: string]: string } = {
      'RED': '빨강',
      'BLUE': '파랑', 
      'GREEN': '초록',
      'YELLOW': '노랑',
      'BLACK': '검정',
      'COLORLESS': '무색'
    };
    return colorMap[color] || color;
  };

  if (loading) {
    return (
      <Box>
        <Navigation onLoginClick={() => setLoginModalOpen(true)} />
        <Container maxWidth="lg" sx={{ py: 4 }}>
          <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
            <Box textAlign="center">
              <CircularProgress size={60} />
              <Typography variant="h6" sx={{ mt: 2 }}>
                카드 데이터를 불러오는 중...
              </Typography>
            </Box>
          </Box>
        </Container>
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Navigation onLoginClick={() => setLoginModalOpen(true)} />
        <Container maxWidth="lg" sx={{ py: 4 }}>
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        </Container>
      </Box>
    );
  }

  return (
    <Box>
      <Navigation onLoginClick={() => setLoginModalOpen(true)} />
      <Container maxWidth="lg" sx={{ py: 4 }}>
        {/* 제목 */}
        <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 4, textAlign: 'center' }}>
          카드 검색
        </Typography>

        {/* 검색바 */}
        <Paper elevation={3} sx={{ p: 3, mb: 4, borderRadius: 2 }}>
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', mb: 2 }}>
            <TextField
              fullWidth
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="카드명을 입력하세요..."
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
            />
            <Box
              onClick={() => setFiltersOpen(!filtersOpen)}
              sx={{
                display: 'flex',
                alignItems: 'center',
                cursor: 'pointer',
                padding: '8px 16px',
                borderRadius: 1,
                border: '1px solid',
                borderColor: 'divider',
                '&:hover': { backgroundColor: 'action.hover' }
              }}
            >
              <FilterList sx={{ mr: 1 }} />
              <Typography>필터</Typography>
              {filtersOpen ? <ExpandLess /> : <ExpandMore />}
            </Box>
          </Box>

          <Collapse in={filtersOpen}>
            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
              <FormControl sx={{ minWidth: 150 }}>
                <InputLabel>희귀도</InputLabel>
                <Select value={rarity} onChange={(e) => setRarity(e.target.value)}>
                  <MenuItem value="all">전체</MenuItem>
                  <MenuItem value="COMMON">커먼</MenuItem>
                  <MenuItem value="RARE">레어</MenuItem>
                  <MenuItem value="SUPER_RARE">슈퍼레어</MenuItem>
                  <MenuItem value="ULTRA_RARE">울트라레어</MenuItem>
                  <MenuItem value="SECRET_RARE">시크릿레어</MenuItem>
                  <MenuItem value="LEGENDARY">레전더리</MenuItem>
                </Select>
              </FormControl>

              <FormControl sx={{ minWidth: 150 }}>
                <InputLabel>색깔</InputLabel>
                <Select value={color} onChange={(e) => setColor(e.target.value)}>
                  <MenuItem value="all">전체</MenuItem>
                  <MenuItem value="RED">빨강</MenuItem>
                  <MenuItem value="BLUE">파랑</MenuItem>
                  <MenuItem value="GREEN">초록</MenuItem>
                  <MenuItem value="YELLOW">노랑</MenuItem>
                  <MenuItem value="BLACK">검정</MenuItem>
                  <MenuItem value="COLORLESS">무색</MenuItem>
                </Select>
              </FormControl>

              <FormControl sx={{ minWidth: 150 }}>
                <InputLabel>비용</InputLabel>
                <Select value={cost} onChange={(e) => setCost(e.target.value)}>
                  <MenuItem value="all">전체</MenuItem>
                  <MenuItem value="0-3">0-3</MenuItem>
                  <MenuItem value="4-6">4-6</MenuItem>
                  <MenuItem value="7+">7+</MenuItem>
                </Select>
              </FormControl>
            </Box>
          </Collapse>
        </Paper>

        {/* 검색 결과 */}
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
                      image={card.images && card.images.length > 0 
                        ? `http://localhost:8090/api/v1/cards/images/${card.images[0].imageId}`
                        : `https://via.placeholder.com/250x350/cccccc/ffffff?text=${encodeURIComponent(card.cardName)}`}
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

        {/* 페이지네이션 */}
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <Pagination 
            count={Math.ceil(filteredCards.length / 8)} 
            color="primary"
            size="large"
          />
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

export default CardSearchPage;