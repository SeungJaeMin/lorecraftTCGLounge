import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  Typography,
  Divider,
  CircularProgress,
  Alert
} from '@mui/material';
import {
  Google,
  Apple,
  Facebook
} from '@mui/icons-material';
import { authAPI } from '../services/api';

interface LoginModalProps {
  open: boolean;
  onClose: () => void;
}

const LoginModal: React.FC<LoginModalProps> = ({ open, onClose }) => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loginError, setLoginError] = useState('');
  const [isLogging, setIsLogging] = useState(false);

  const handleLogin = async () => {
    if (!username || !password) {
      setLoginError('아이디와 비밀번호를 입력해주세요.');
      return;
    }

    setIsLogging(true);
    setLoginError('');

    try {
      console.log('LoginModal 로그인 시도:', username, password);
      const response = await authAPI.login(username, password);
      console.log('LoginModal 로그인 응답:', response);

      if (response.data.success) {
        // 토큰 저장
        localStorage.setItem('tcg_lounge_token', response.data.data.token);
        localStorage.setItem('tcg_lounge_user', JSON.stringify(response.data.data));
        localStorage.setItem('userType', response.data.data.userType);
        localStorage.setItem('username', response.data.data.username);
        
        handleCloseModal();
        
        // 사용자 유형에 따라 리다이렉트
        const userType = response.data.data.userType;
        switch (userType) {
          case 'GAMER':
            navigate('/gamer-lounge');
            break;
          case 'STORE_OWNER':
            navigate('/store-owner-lounge');
            break;
          case 'ADMIN':
            navigate('/admin');
            break;
          default:
            navigate('/store-owner-lounge');
        }
      } else {
        setLoginError(response.data.message || '로그인에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('LoginModal 로그인 오류:', error);
      setLoginError(error.response?.data?.message || '로그인에 실패했습니다.');
    } finally {
      setIsLogging(false);
    }
  };

  const handleCloseModal = () => {
    onClose();
    setUsername('');
    setPassword('');
    setLoginError('');
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleCloseModal}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle sx={{ textAlign: 'center', fontWeight: 'bold', fontSize: '1.5rem' }}>
        ESTELA ⭐ 로그인
      </DialogTitle>
      <DialogContent sx={{ pt: 3 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {loginError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {loginError}
            </Alert>
          )}
          <TextField
            fullWidth
            label="아이디"
            variant="outlined"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            disabled={isLogging}
            sx={{ mt: 1 }}
          />
          <TextField
            fullWidth
            label="비밀번호"
            type="password"
            variant="outlined"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={isLogging}
            onKeyPress={(e) => {
              if (e.key === 'Enter' && !isLogging) {
                handleLogin();
              }
            }}
          />
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Button sx={{ color: '#666', fontSize: '0.9rem' }}>
              아이디/비밀번호 찾기
            </Button>
            <Button sx={{ color: '#666', fontSize: '0.9rem' }}>
              회원가입
            </Button>
          </Box>
          
          {/* 구분선 */}
          <Box sx={{ display: 'flex', alignItems: 'center', my: 2 }}>
            <Divider sx={{ flex: 1 }} />
            <Typography sx={{ px: 2, color: '#999', fontSize: '0.9rem' }}>
              또는
            </Typography>
            <Divider sx={{ flex: 1 }} />
          </Box>
          
          {/* 소셜 로그인 버튼들 */}
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Button
              fullWidth
              variant="outlined"
              startIcon={<Google />}
              sx={{
                py: 1.5,
                borderColor: '#ddd',
                color: '#333',
                '&:hover': {
                  borderColor: '#4285f4',
                  backgroundColor: '#4285f4',
                  color: 'white'
                }
              }}
            >
              Google로 로그인
            </Button>
            
            <Button
              fullWidth
              variant="outlined"
              startIcon={<Facebook />}
              sx={{
                py: 1.5,
                borderColor: '#ddd',
                color: '#333',
                '&:hover': {
                  borderColor: '#1877f2',
                  backgroundColor: '#1877f2',
                  color: 'white'
                }
              }}
            >
              Facebook으로 로그인
            </Button>
            
            <Button
              fullWidth
              variant="outlined"
              startIcon={<Apple />}
              sx={{
                py: 1.5,
                borderColor: '#ddd',
                color: '#333',
                '&:hover': {
                  borderColor: '#000',
                  backgroundColor: '#000',
                  color: 'white'
                }
              }}
            >
              Apple로 로그인
            </Button>
            
            <Button
              fullWidth
              variant="outlined"
              sx={{
                py: 1.5,
                borderColor: '#ddd',
                color: '#333',
                backgroundColor: '#fee500',
                '&:hover': {
                  backgroundColor: '#fdd835',
                  borderColor: '#fdd835'
                }
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <img 
                  src="https://developers.kakao.com/assets/img/about/logos/kakaolink/kakaolink_btn_small.png" 
                  alt="Kakao" 
                  style={{ width: '20px', height: '20px' }}
                />
                <Typography>카카오로 로그인</Typography>
              </Box>
            </Button>
            
            <Button
              fullWidth
              variant="outlined"
              sx={{
                py: 1.5,
                borderColor: '#ddd',
                color: '#333',
                backgroundColor: '#03c75a',
                '&:hover': {
                  backgroundColor: '#02b350',
                  borderColor: '#02b350'
                }
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, color: 'white' }}>
                <Typography sx={{ fontWeight: 'bold', fontSize: '1.1rem' }}>N</Typography>
                <Typography>네이버로 로그인</Typography>
              </Box>
            </Button>
          </Box>
        </Box>
      </DialogContent>
      <DialogActions sx={{ p: 3, pt: 1 }}>
        <Button 
          onClick={handleCloseModal}
          disabled={isLogging}
          sx={{ 
            flex: 1, 
            py: 1.5,
            borderColor: '#ddd', 
            color: '#666',
            '&:hover': { borderColor: '#b8191c', color: '#b8191c' }
          }}
          variant="outlined"
        >
          취소
        </Button>
        <Button 
          onClick={handleLogin}
          disabled={isLogging}
          sx={{ 
            flex: 1, 
            py: 1.5,
            backgroundColor: '#b8191c',
            '&:hover': { backgroundColor: '#a01018' }
          }}
          variant="contained"
          startIcon={isLogging ? <CircularProgress size={20} color="inherit" /> : undefined}
        >
          {isLogging ? '로그인 중...' : '로그인'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default LoginModal;