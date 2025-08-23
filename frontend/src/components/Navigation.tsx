import React from 'react';
import {
  AppBar,
  Toolbar,
  Button,
  Box,
  Typography
} from '@mui/material';
import {
  Login
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

interface NavigationProps {
  onLoginClick?: () => void;
}

const Navigation: React.FC<NavigationProps> = ({ onLoginClick }) => {
  const navigate = useNavigate();

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
          flex: '0 0 140px', 
          backgroundColor: 'black', 
          height: '80px',
          display: 'flex', 
          alignItems: 'center',
          justifyContent: 'center',
          gap: 2,
          ml: 'auto'
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
      </Toolbar>
    </AppBar>
  );
};

export default Navigation;