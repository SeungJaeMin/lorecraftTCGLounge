import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI, gamerAPI } from '../services/api';
import './GamerLoungeNavigation.css';

interface NavMenuItem {
  label: string;
  path: string;
}

interface NavCategory {
  title: string;
  items: NavMenuItem[];
}

const GamerLoungeNavigation: React.FC = () => {
  const navigate = useNavigate();
  const [clickedDropdown, setClickedDropdown] = useState<string | null>(null);
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
        { label: '마이페이지', path: '/my-page' },
        { label: '프로필 수정', path: '/profile-edit' },
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
        setClickedDropdown(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleNavButtonClick = (category: string) => {
    if (clickedDropdown === category) {
      setClickedDropdown(null);
    } else {
      setClickedDropdown(category);
    }
  };

  const handleNavItemClick = (path: string) => {
    navigate(path);
    setClickedDropdown(null);
  };

  const handleLogout = async () => {
    try {
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      localStorage.removeItem('tcg_lounge_token');
      localStorage.removeItem('tcg_lounge_user');
      navigate('/login');
    }
  };

  const handleHomeClick = () => {
    navigate('/gamer-lounge');
  };

  return (
    <header className="gamer-lounge-header">
      <div className="header-container">
        <div className="header-left">
          <h1 className="lounge-title" onClick={handleHomeClick}>
            게이머라운지
          </h1>
        </div>

        <nav className="main-nav" ref={dropdownRef}>
          {Object.keys(navCategories).map((key) => (
            <div
              key={key}
              className="nav-item"
            >
              <button 
                className={`nav-button ${clickedDropdown === key ? 'active' : ''}`}
                onClick={() => handleNavButtonClick(key)}
              >
                {navCategories[key].title}
                <span className="dropdown-arrow">▼</span>
              </button>
              {clickedDropdown === key && (
                <div className="dropdown-menu">
                  {navCategories[key].items.map((item) => (
                    <button
                      key={item.path}
                      className="dropdown-item"
                      onClick={() => handleNavItemClick(item.path)}
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
            {userData.profileImage ? (
              <img 
                src={userData.profileImage} 
                alt="Profile" 
                className="profile-image"
              />
            ) : (
              <div className="profile-placeholder">
                {userData.nickname.charAt(0).toUpperCase()}
              </div>
            )}
            <span className="user-name">{userData.nickname}</span>
            <button className="logout-button" onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

export default GamerLoungeNavigation;