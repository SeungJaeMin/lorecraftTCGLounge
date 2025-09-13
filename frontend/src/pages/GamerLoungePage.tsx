import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI, gamerAPI } from '../services/api';
import './GamerLoungePage.css';

interface NavMenuItem {
  label: string;
  path: string;
}

interface NavCategory {
  title: string;
  items: NavMenuItem[];
}

const GamerLoungePage: React.FC = () => {
  const navigate = useNavigate();
  const [activeDropdown, setActiveDropdown] = useState<string | null>(null);
  const [userData, setUserData] = useState({
    nickname: '게이머',
    profileImage: null as string | null
  });
  const dropdownRef = useRef<HTMLDivElement>(null);

  const navCategories: { [key: string]: NavCategory } = {
    news: {
      title: '새소식',
      items: [
        { label: '공지사항', path: '/announcements' },
        { label: '제품정보', path: '/products' },
        { label: '점포별소식', path: '/store-news' }
      ]
    },
    deck: {
      title: '덱',
      items: [
        { label: '내 덱', path: '/my-decks' },
        { label: '덱 검색', path: '/deck-search' },
        { label: '카드검색', path: '/card-search' }
      ]
    },
    event: {
      title: '이벤트',
      items: [
        { label: '통합일정', path: '/calendar' },
        { label: '대회참가', path: '/tournament' },
        { label: '이벤트참가', path: '/events' }
      ]
    },
    data: {
      title: '데이터',
      items: [
        { label: '플레이어 랭킹', path: '/rankings' },
        { label: '공식점포 목록', path: '/stores' }
      ]
    },
    mypage: {
      title: '마이페이지',
      items: [
        { label: '내 정보 보기', path: '/my-info' },
        { label: '내 대회 보기', path: '/my-tournaments' },
        { label: '도전과제 & 칭호', path: '/achievements' }
      ]
    }
  };

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await gamerAPI.getDashboard();
        if (response.data.success) {
          const profile = response.data.data.profile;
          setUserData({
            nickname: profile.nickname || '게이머',
            profileImage: profile.profileImage || null
          });
        }
      } catch (error) {
        console.error('Failed to fetch user data:', error);
      }
    };

    fetchUserData();

    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setActiveDropdown(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleNavHover = (category: string) => {
    setActiveDropdown(category);
  };

  const handleNavLeave = () => {
    setActiveDropdown(null);
  };

  const handleNavClick = (path: string) => {
    navigate(path);
    setActiveDropdown(null);
  };

  const handleLogout = async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('userType');
      localStorage.removeItem('username');
      navigate('/login');
    }
  };

  return (
    <div className="gamer-lounge">
      <header className="lounge-header">
        <div className="header-container">
          <div className="header-left">
            <h1 className="lounge-title">게이머라운지</h1>
          </div>

          <nav className="main-nav" ref={dropdownRef}>
            {Object.keys(navCategories).map((key) => (
              <div
                key={key}
                className="nav-item"
                onMouseEnter={() => handleNavHover(key)}
                onMouseLeave={handleNavLeave}
              >
                <button className="nav-button">
                  {navCategories[key].title}
                </button>
                {activeDropdown === key && (
                  <div className="dropdown-menu">
                    {navCategories[key].items.map((item) => (
                      <button
                        key={item.path}
                        className="dropdown-item"
                        onClick={() => handleNavClick(item.path)}
                      >
                        {item.label}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </nav>

          <div className="header-right">
            <div className="user-profile">
              <div className="profile-image">
                {userData.profileImage ? (
                  <img src={userData.profileImage} alt="Profile" />
                ) : (
                  <div className="profile-placeholder">
                    {userData.nickname.charAt(0)}
                  </div>
                )}
              </div>
              <span className="user-nickname">{userData.nickname}</span>
              <button className="logout-btn" onClick={handleLogout}>
                로그아웃
              </button>
            </div>
          </div>
        </div>
      </header>

      <main className="lounge-content">
        <div className="welcome-section">
          <h2>환영합니다, {userData.nickname}님!</h2>
          <p>게이머 라운지에서 덱을 관리하고 대회에 참가해보세요.</p>
        </div>

        <div className="quick-access">
          <h3>빠른 메뉴</h3>
          <div className="quick-menu-grid">
            {Object.keys(navCategories).map((key) => (
              <div key={key} className="quick-menu-category">
                <h4>{navCategories[key].title}</h4>
                <ul>
                  {navCategories[key].items.map((item) => (
                    <li key={item.path}>
                      <button 
                        className="quick-link"
                        onClick={() => handleNavClick(item.path)}
                      >
                        {item.label}
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
};

export default GamerLoungePage;