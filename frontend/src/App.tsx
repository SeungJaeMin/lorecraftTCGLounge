import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import MainPage from './pages/MainPage';
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
            <Route path="/" element={<MainPage />} />
            <Route path="/gamer-lounge" element={<GamerLoungePage />} />
            <Route path="/store-owner-lounge" element={<StoreOwnerLoungePage />} />
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/news" element={<NewsPage />} />
            <Route path="/products" element={<ProductInfoPage />} />
            <Route path="/how-to-play" element={<HowToPlayPage />} />
            <Route path="/card-search" element={<CardSearchPage />} />
            <Route path="/events" element={<EventPage />} />
            <Route path="/tournaments" element={<TournamentPage />} />
            <Route path="/ranking" element={<RankingPage />} />
          </Routes>
        </div>
      </Router>
    </ThemeProvider>
  );
}

export default App;
