import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import GamerLoungePage from './pages/GamerLoungePage';
import StoreOwnerLoungePage from './pages/StoreOwnerLoungePage';
import AdminPage from './pages/AdminPage';
import NewsPage from './pages/NewsPage';
import ProductInfoPage from './pages/ProductInfoPage';
import HowToPlayPage from './pages/HowToPlayPage';
import CardSearchPage from './pages/CardSearchPage';
import EventPage from './pages/EventPage';
import TournamentPage from './pages/TournamentPage';
import RankingPage from './pages/RankingPage';
import ImageUploadTest from './pages/ImageUploadTest';
import MyPage from './pages/MyPage';
import MyDecks from './pages/MyDecks';
import DeckEditor from './pages/DeckEditor';
import ProfileEdit from './pages/ProfileEdit';
import ProtectedRoute from './components/ProtectedRoute';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontWeight: 700,
    },
    h2: {
      fontWeight: 600,
    },
    h3: {
      fontWeight: 600,
    },
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 500,
    },
    h6: {
      fontWeight: 500,
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <div className="App">
          <Routes>
            {/* 공개 페이지 */}
            <Route path="/" element={<MainPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/news" element={<NewsPage />} />
            <Route path="/products" element={<ProductInfoPage />} />
            <Route path="/how-to-play" element={<HowToPlayPage />} />
            <Route path="/card-search" element={<CardSearchPage />} />
            <Route path="/events" element={<EventPage />} />
            <Route path="/tournaments" element={<TournamentPage />} />
            <Route path="/ranking" element={<RankingPage />} />
            
            {/* 보호된 페이지 - 로그인 필요 */}
            <Route 
              path="/gamer-lounge" 
              element={
                <ProtectedRoute requiredUserType="GAMER">
                  <GamerLoungePage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/store-owner-lounge" 
              element={
                <ProtectedRoute requiredUserType="STORE_OWNER">
                  <StoreOwnerLoungePage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/admin" 
              element={
                <ProtectedRoute requiredUserType="ADMIN">
                  <AdminPage />
                </ProtectedRoute>
              } 
            />
            
            {/* 사용자 관련 페이지 - 로그인 필요 */}
            <Route 
              path="/my-page" 
              element={
                <ProtectedRoute>
                  <MyPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/my-decks" 
              element={
                <ProtectedRoute>
                  <MyDecks />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/deck-editor/:deckId" 
              element={
                <ProtectedRoute>
                  <DeckEditor />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/profile-edit" 
              element={
                <ProtectedRoute>
                  <ProfileEdit />
                </ProtectedRoute>
              } 
            />
            
            {/* 개발/테스트 페이지 */}
            <Route 
              path="/image-upload-test" 
              element={
                <ProtectedRoute>
                  <ImageUploadTest />
                </ProtectedRoute>
              } 
            />
          </Routes>
        </div>
      </Router>
    </ThemeProvider>
  );
}

export default App;
