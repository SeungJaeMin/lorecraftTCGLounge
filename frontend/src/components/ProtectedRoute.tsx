import React, { useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { Box, CircularProgress, Typography } from '@mui/material';
import { authAPI } from '../services/api';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredUserType?: 'GAMER' | 'ADMIN' | 'STORE_OWNER';
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredUserType 
}) => {
  const location = useLocation();
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userType, setUserType] = useState<string | null>(null);

  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('tcg_lounge_token');
      const storedUserType = localStorage.getItem('userType');
      
      if (!token) {
        setIsLoading(false);
        return;
      }

      // 로그인 직후라면 저장된 userType을 바로 사용
      if (storedUserType) {
        setIsAuthenticated(true);
        setUserType(storedUserType);
        setIsLoading(false);
        return;
      }

      try {
        const response = await authAPI.getCurrentUser();
        
        if (response.data.success) {
          setIsAuthenticated(true);
          setUserType(response.data.data.userType);
        } else {
          // 토큰이 유효하지 않음
          localStorage.removeItem('tcg_lounge_token');
          localStorage.removeItem('userType');
          localStorage.removeItem('username');
        }
      } catch (error) {
        // 인증 실패
        localStorage.removeItem('tcg_lounge_token');
        localStorage.removeItem('userType');
        localStorage.removeItem('username');
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, []);

  // 로딩 중
  if (isLoading) {
    return (
      <Box 
        sx={{ 
          display: 'flex', 
          flexDirection: 'column',
          justifyContent: 'center', 
          alignItems: 'center', 
          minHeight: '100vh',
          gap: 2
        }}
      >
        <CircularProgress size={50} />
        <Typography variant="body1" color="text.secondary">
          인증 확인 중...
        </Typography>
      </Box>
    );
  }

  // 인증되지 않은 경우 로그인 페이지로 리다이렉트
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 특정 사용자 타입이 필요한 경우 권한 확인
  if (requiredUserType && userType !== requiredUserType) {
    // 권한이 없는 경우 적절한 페이지로 리다이렉트
    if (userType === 'GAMER') {
      return <Navigate to="/gamer-lounge" replace />;
    } else if (userType === 'ADMIN') {
      return <Navigate to="/admin" replace />;
    } else {
      return <Navigate to="/store-owner-lounge" replace />;
    }
  }

  // 인증되고 권한이 있는 경우 컴포넌트 렌더링
  return <>{children}</>;
};

export default ProtectedRoute;