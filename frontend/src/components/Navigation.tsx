import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Button,
  Box,
  Typography,
  IconButton,
  TextField,
  InputAdornment,
  Collapse,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
import {
  Login,
  Search,
  ExpandMore
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

interface NavigationProps {
  onLoginClick?: () => void;
}

const Navigation: React.FC<NavigationProps> = ({ onLoginClick }) => {
  const navigate = useNavigate();
  const [searchExpanded, setSearchExpanded] = useState(false);
  const [showSearchOptions, setShowSearchOptions] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchType, setSearchType] = useState('all');
  const [searchRarity, setSearchRarity] = useState('all');
  const [searchColor, setSearchColor] = useState('all');

  const handleSearch = () => {
    const params = new URLSearchParams();
    if (searchQuery.trim()) params.set('query', searchQuery.trim());
    if (searchType !== 'all') params.set('type', searchType);
    if (searchRarity !== 'all') params.set('rarity', searchRarity);
    if (searchColor !== 'all') params.set('color', searchColor);
    
    navigate(`/card-search?${params.toString()}`);
    setSearchExpanded(false);
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <AppBar position="fixed" sx={{ backgroundColor: 'white', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
      <Toolbar sx={{ px: 0, minHeight: '80px !important', height: '80px', display: 'flex', alignItems: 'center' }}>
        <Box sx={{ flex: '0 0 200px', pl: 3, display: 'flex', alignItems: 'center' }}>
          <Typography 
            variant="h5" 
            sx={{ color: 'black', fontWeight: 'bold', cursor: 'pointer' }}
            onClick={() => navigate('/')}
          >
            ESTELA ⭐
          </Typography>
        </Box>
        
        <Box sx={{ 
          display: 'flex', 
          gap: 3, 
          flexGrow: 1, 
          justifyContent: 'center',
          alignItems: 'center',
          flexWrap: 'nowrap'
        }}>
          <Button 
            onClick={() => navigate('/news')}
            sx={{ color: 'black', fontSize: '1rem', fontWeight: '500' }}
          >
            새소식
          </Button>
          <Button 
            onClick={() => navigate('/products')}
            sx={{ color: 'black', fontSize: '1rem', fontWeight: '500' }}
          >
            제품정보
          </Button>
          <Button 
            onClick={() => navigate('/how-to-play')}
            sx={{ color: 'black', fontSize: '1rem', fontWeight: '500' }}
          >
            놀이방법
          </Button>
          <Button 
            onClick={() => navigate('/card-search')}
            sx={{ color: 'black', fontSize: '1rem', fontWeight: '500' }}
          >
            카드검색
          </Button>
          <Button 
            onClick={() => navigate('/events')}
            sx={{ color: 'black', fontSize: '1rem', fontWeight: '500' }}
          >
            이벤트
          </Button>
          <Button 
            onClick={() => navigate('/tournaments')}
            sx={{ color: 'black', fontSize: '1rem', fontWeight: '500' }}
          >
            대회예약
          </Button>
          <Button 
            onClick={() => navigate('/ranking')}
            sx={{ color: 'black', fontSize: '1rem', fontWeight: '500' }}
          >
            플레이어 랭킹
          </Button>
        </Box>

        <Box sx={{ 
          display: 'flex',
          alignItems: 'center',
          ml: 'auto'
        }}>
          {/* 검색 아이콘 + 카드검색 버튼 */}
          <Box sx={{ 
            backgroundColor: '#b8191c',
            height: '80px',
            display: 'flex',
            alignItems: 'center',
            px: 2
          }}>
            <Button
              startIcon={<Search />}
              endIcon={<ExpandMore sx={{ transform: searchExpanded ? 'rotate(180deg)' : 'rotate(0deg)', transition: 'transform 0.3s' }} />}
              onClick={() => setSearchExpanded(!searchExpanded)}
              sx={{ 
                color: 'white', 
                fontSize: '1rem', 
                fontWeight: '500',
                '&:hover': { 
                  backgroundColor: 'rgba(255,255,255,0.1)'
                }
              }}
            >
              카드검색
            </Button>
          </Box>

          {/* 로그인 버튼 영역 */}
          <Box sx={{ 
            flex: '0 0 140px', 
            backgroundColor: 'black', 
            height: '80px',
            display: 'flex', 
            alignItems: 'center',
            justifyContent: 'center',
            gap: 2
          }}>
            <Button 
              startIcon={<Login />} 
              onClick={onLoginClick}
              sx={{ 
                color: 'white', 
                fontSize: '1rem', 
                fontWeight: '500',
                '&:hover': { backgroundColor: 'rgba(255,255,255,0.1)' } 
              }}
            >
              로그인
            </Button>
          </Box>
        </Box>
      </Toolbar>
      
      {/* 서랍식 검색 영역 */}
      <Collapse in={searchExpanded}>
        <Box sx={{ 
          backgroundColor: 'white', 
          borderTop: '1px solid #ddd',
          px: 3,
          py: 2,
          boxShadow: '0 4px 6px rgba(0,0,0,0.1)'
        }}>
          <Box sx={{ maxWidth: 'lg', mx: 'auto' }}>
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1 }}>
              <TextField
                fullWidth
                placeholder="카드명을 입력하세요"
                variant="outlined"
                size="small"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyPress={handleKeyPress}
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton size="small" onClick={handleSearch}>
                        <Search />
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
              <Button
                onClick={() => setShowSearchOptions(!showSearchOptions)}
                sx={{ 
                  minWidth: '120px',
                  backgroundColor: '#b8191c',
                  color: 'white',
                  '&:hover': { 
                    backgroundColor: '#a01018'
                  }
                }}
              >
                검색 옵션
              </Button>
            </Box>
            
            {/* 검색 옵션 */}
            <Collapse in={showSearchOptions}>
              <Box sx={{ 
                display: 'flex', 
                gap: 2, 
                mt: 2,
                p: 2,
                backgroundColor: '#f5f5f5',
                borderRadius: 1
              }}>
                <FormControl size="small" sx={{ minWidth: 120 }}>
                  <InputLabel>카드 타입</InputLabel>
                  <Select 
                    label="카드 타입"
                    value={searchType}
                    onChange={(e) => setSearchType(e.target.value)}
                  >
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="UNIT">유닛</MenuItem>
                    <MenuItem value="SPELL">마법</MenuItem>
                    <MenuItem value="LEADER">리더</MenuItem>
                  </Select>
                </FormControl>
                
                <FormControl size="small" sx={{ minWidth: 120 }}>
                  <InputLabel>희귀도</InputLabel>
                  <Select 
                    label="희귀도"
                    value={searchRarity}
                    onChange={(e) => setSearchRarity(e.target.value)}
                  >
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="COMMON">일반</MenuItem>
                    <MenuItem value="RARE">희귀</MenuItem>
                    <MenuItem value="LEGENDARY">전설</MenuItem>
                  </Select>
                </FormControl>

                <FormControl size="small" sx={{ minWidth: 120 }}>
                  <InputLabel>색상</InputLabel>
                  <Select 
                    label="색상"
                    value={searchColor}
                    onChange={(e) => setSearchColor(e.target.value)}
                  >
                    <MenuItem value="all">전체</MenuItem>
                    <MenuItem value="RED">빨강</MenuItem>
                    <MenuItem value="BLUE">파랑</MenuItem>
                    <MenuItem value="GREEN">초록</MenuItem>
                  </Select>
                </FormControl>
              </Box>
            </Collapse>
          </Box>
        </Box>
      </Collapse>
    </AppBar>
  );
};

export default Navigation;