import React, { useState } from 'react';
import {
  Container,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
  Link
} from '@mui/material';
import {
  Login,
  Visibility,
  VisibilityOff
} from '@mui/icons-material';
import { IconButton, InputAdornment } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';

interface LoginForm {
  username: string;
  password: string;
}

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  // const [tabValue, setTabValue] = useState(0);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [loginForm, setLoginForm] = useState<LoginForm>({
    username: '',
    password: ''
  });

  // const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
  //   setTabValue(newValue);
  //   setError('');
  //   setSuccess('');
  // };

  const handleInputChange = (field: keyof LoginForm) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setLoginForm({
      ...loginForm,
      [field]: event.target.value
    });
    setError('');
  };

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      console.log('로그인 시도:', loginForm.username, loginForm.password);
      const response = await authAPI.login(loginForm.username, loginForm.password);
      console.log('로그인 응답:', response);
      
      if (response.data.success) {
        // 토큰 저장
        localStorage.setItem('tcg_lounge_token', response.data.data.token);
        localStorage.setItem('tcg_lounge_user', JSON.stringify(response.data.data));
        localStorage.setItem('userType', response.data.data.userType);
        localStorage.setItem('username', response.data.data.username);
        
        setSuccess('로그인 성공! 리다이렉트 중...');
        
        // 사용자 타입에 따라 리다이렉트
        setTimeout(() => {
          if (response.data.data.userType === 'GAMER') {
            navigate('/gamer-lounge');
          } else if (response.data.data.userType === 'ADMIN') {
            navigate('/admin');
          } else {
            navigate('/store-owner-lounge');
          }
        }, 1500);
      } else {
        setError(response.data.message || '로그인에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('로그인 오류:', error);
      console.error('오류 응답:', error.response);
      setError(error.response?.data?.message || '로그인 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 프로덕션에서는 테스트 계정 기능 제거
  // const handleTestLogin = (username: string, password: string, userType: string) => {
  //   // 테스트 계정 로그인 로직 제거
  // };

  return (
    <Box sx={{ 
      minHeight: '100vh', 
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      py: 4
    }}>
      <Container maxWidth="sm">
        <Box sx={{ textAlign: 'center', mb: 4 }}>
          <Typography variant="h3" sx={{ 
            color: 'white', 
            fontWeight: 'bold',
            textShadow: '2px 2px 4px rgba(0,0,0,0.3)',
            mb: 1
          }}>
            ESTELA ⭐
          </Typography>
          <Typography variant="h6" sx={{ 
            color: 'rgba(255,255,255,0.9)',
            textShadow: '1px 1px 2px rgba(0,0,0,0.3)'
          }}>
            TCG 라운지에 오신 것을 환영합니다
          </Typography>
        </Box>

        <Card sx={{ 
          borderRadius: 3,
          boxShadow: '0 20px 40px rgba(0,0,0,0.1)',
          overflow: 'hidden'
        }}>
          {/* 프로덕션에서는 탭 제거 - 로그인만 제공 */}

          <CardContent sx={{ p: 4 }}>
            <Box component="form" onSubmit={handleLogin}>
                <TextField
                  fullWidth
                  label="사용자명"
                  value={loginForm.username}
                  onChange={handleInputChange('username')}
                  margin="normal"
                  required
                  autoComplete="username"
                  sx={{ mb: 2 }}
                />
                
                <TextField
                  fullWidth
                  label="비밀번호"
                  type={showPassword ? 'text' : 'password'}
                  value={loginForm.password}
                  onChange={handleInputChange('password')}
                  margin="normal"
                  required
                  autoComplete="current-password"
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowPassword(!showPassword)}
                          edge="end"
                        >
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  sx={{ mb: 3 }}
                />

                {error && (
                  <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                  </Alert>
                )}

                {success && (
                  <Alert severity="success" sx={{ mb: 2 }}>
                    {success}
                  </Alert>
                )}

                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  disabled={loading}
                  sx={{ 
                    py: 1.5,
                    fontSize: '1.1rem',
                    background: 'linear-gradient(45deg, #b8191c 30%, #f4c87a 90%)',
                    '&:hover': {
                      background: 'linear-gradient(45deg, #a01018 30%, #f4c87a 90%)',
                    }
                  }}
                >
                  {loading ? (
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <CircularProgress size={20} color="inherit" />
                      로그인 중...
                    </Box>
                  ) : (
                    '로그인'
                  )}
                </Button>

                <Box sx={{ textAlign: 'center', mt: 2 }}>
                  <Link
                    href="#"
                    onClick={() => navigate('/')}
                    sx={{ 
                      color: 'text.secondary',
                      textDecoration: 'none',
                      '&:hover': { textDecoration: 'underline' }
                    }}
                  >
                    메인 페이지로 돌아가기
                  </Link>
                </Box>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
};

export default LoginPage;