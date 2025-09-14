import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { gamerAPI } from '../services/api';
import './MyPage.css';

interface UserProfile {
  nickname: string;
  email: string;
  totalWins: number;
  totalLosses: number;
  totalDraws: number;
  winRate: number;
  currentRating: number;
  usablePoint: number;
}

interface DeckInfo {
  id: number;
  deckName: string;
  totalCards: number;
  isComplete: boolean;
  leaderCard?: {
    cardName: string;
    cardColor: string;
  };
  updatedAt: string;
}

interface Tournament {
  id: number;
  name: string;
  status: 'UPCOMING' | 'ONGOING' | 'COMPLETED';
  startDate: string;
  prize: string;
}

const MyPage: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'profile' | 'record'>('profile');
  const [loading, setLoading] = useState(true);
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
  const [myDecks, setMyDecks] = useState<DeckInfo[]>([]);
  const [tournaments, setTournaments] = useState<Tournament[]>([]);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const response = await gamerAPI.getDashboard();
      
      if (response.data.success) {
        const data = response.data.data;
        setUserProfile(data.profile);
        setMyDecks(data.myDecks || []);
        setTournaments(data.tournaments || []);
      }
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDeckClick = (deckId: number) => {
    navigate(`/deck-editor/${deckId}`);
  };

  const handleCreateDeck = () => {
    navigate('/deck-editor/new');
  };

  const handleViewAllDecks = () => {
    navigate('/my-decks');
  };

  const handleViewProfile = () => {
    navigate('/profile-edit');
  };

  const handleViewRecord = () => {
    navigate('/my-record');
  };

  if (loading) {
    return (
      <div className="mypage-loading">
        <div className="loading-spinner"></div>
        <p>마이페이지를 불러오는 중...</p>
      </div>
    );
  }

  if (!userProfile) {
    return (
      <div className="mypage-error">
        <p>사용자 정보를 불러올 수 없습니다.</p>
        <button onClick={() => window.location.reload()}>다시 시도</button>
      </div>
    );
  }

  const upcomingTournaments = tournaments.filter(t => t.status === 'UPCOMING');
  const ongoingTournaments = tournaments.filter(t => t.status === 'ONGOING');
  const completedTournaments = tournaments.filter(t => t.status === 'COMPLETED');

  return (
    <div className="mypage">
      <div className="mypage-container">
        <h1 className="mypage-title">마이페이지</h1>

        {/* 상단 탭 */}
        <div className="info-section">
          <div className="tab-header">
            <button 
              className={`tab-button ${activeTab === 'profile' ? 'active' : ''}`}
              onClick={() => setActiveTab('profile')}
            >
              내 기본정보
            </button>
            <button 
              className={`tab-button ${activeTab === 'record' ? 'active' : ''}`}
              onClick={() => setActiveTab('record')}
            >
              내 전적
            </button>
          </div>

          <div className="tab-content">
            {activeTab === 'profile' && (
              <div className="profile-info">
                <div className="info-item">
                  <label>닉네임:</label>
                  <span>{userProfile.nickname}</span>
                </div>
                <div className="info-item">
                  <label>이메일:</label>
                  <span>{userProfile.email}</span>
                </div>
                <div className="info-item">
                  <label>보유 포인트:</label>
                  <span className="points">{userProfile.usablePoint?.toLocaleString() || 0} P</span>
                </div>
                <button className="detail-button" onClick={handleViewProfile}>
                  자세히보기
                </button>
              </div>
            )}

            {activeTab === 'record' && (
              <div className="record-info">
                <div className="stats-grid">
                  <div className="stat-item wins">
                    <div className="stat-number">{userProfile.totalWins}</div>
                    <div className="stat-label">승리</div>
                  </div>
                  <div className="stat-item losses">
                    <div className="stat-number">{userProfile.totalLosses}</div>
                    <div className="stat-label">패배</div>
                  </div>
                  <div className="stat-item draws">
                    <div className="stat-number">{userProfile.totalDraws}</div>
                    <div className="stat-label">무승부</div>
                  </div>
                  <div className="stat-item winrate">
                    <div className="stat-number">{Math.round(userProfile.winRate)}%</div>
                    <div className="stat-label">승률</div>
                  </div>
                </div>
                <div className="rating-info">
                  <label>현재 레이팅:</label>
                  <span className="rating">{userProfile.currentRating} RP</span>
                </div>
                <button className="detail-button" onClick={handleViewRecord}>
                  자세히보기
                </button>
              </div>
            )}
          </div>
        </div>

        {/* 내 덱 섹션 */}
        <div className="deck-section">
          <div className="section-header">
            <h2>내 덱</h2>
            <button className="view-all-button" onClick={handleViewAllDecks}>
              모든 덱 보기
            </button>
          </div>
          
          <div className="deck-grid">
            {Array.from({ length: 10 }, (_, index) => {
              const deck = myDecks[index];
              const isEmpty = !deck;
              
              return (
                <div
                  key={index}
                  className={`deck-slot ${isEmpty ? 'empty' : 'filled'} ${deck?.isComplete ? 'complete' : 'incomplete'}`}
                  onClick={isEmpty ? handleCreateDeck : () => handleDeckClick(deck.id)}
                  title={deck ? deck.deckName : '새 덱 만들기'}
                >
                  {isEmpty ? (
                    <div className="empty-deck">
                      <div className="plus-icon">+</div>
                      <span>덱 생성</span>
                    </div>
                  ) : (
                    <div className="deck-info">
                      <div className="deck-icon">
                        <div className={`deck-color ${deck.leaderCard?.cardColor?.toLowerCase() || 'default'}`}></div>
                        <div className="card-count">{deck.totalCards}</div>
                      </div>
                      <div className="deck-name">{deck.deckName}</div>
                      {!deck.isComplete && <div className="incomplete-badge">미완성</div>}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* 내 대회 섹션 */}
        <div className="tournament-section">
          <h2>내 대회</h2>
          
          <div className="tournament-tabs">
            <div className="tournament-category">
              <h3>참여 중인 대회 ({ongoingTournaments.length})</h3>
              <div className="tournament-list">
                {ongoingTournaments.length > 0 ? (
                  ongoingTournaments.slice(0, 3).map(tournament => (
                    <div key={tournament.id} className="tournament-item ongoing">
                      <div className="tournament-info">
                        <span className="tournament-name">{tournament.name}</span>
                        <span className="tournament-date">{new Date(tournament.startDate).toLocaleDateString()}</span>
                      </div>
                      <span className="tournament-prize">{tournament.prize}</span>
                    </div>
                  ))
                ) : (
                  <p className="no-tournaments">참여 중인 대회가 없습니다.</p>
                )}
              </div>
            </div>

            <div className="tournament-category">
              <h3>참여 예정 대회 ({upcomingTournaments.length})</h3>
              <div className="tournament-list">
                {upcomingTournaments.length > 0 ? (
                  upcomingTournaments.slice(0, 3).map(tournament => (
                    <div key={tournament.id} className="tournament-item upcoming">
                      <div className="tournament-info">
                        <span className="tournament-name">{tournament.name}</span>
                        <span className="tournament-date">{new Date(tournament.startDate).toLocaleDateString()}</span>
                      </div>
                      <span className="tournament-prize">{tournament.prize}</span>
                    </div>
                  ))
                ) : (
                  <p className="no-tournaments">참여 예정 대회가 없습니다.</p>
                )}
              </div>
            </div>

            <div className="tournament-category">
              <h3>참여 완료한 대회 ({completedTournaments.length})</h3>
              <div className="tournament-list">
                {completedTournaments.length > 0 ? (
                  completedTournaments.slice(0, 3).map(tournament => (
                    <div key={tournament.id} className="tournament-item completed">
                      <div className="tournament-info">
                        <span className="tournament-name">{tournament.name}</span>
                        <span className="tournament-date">{new Date(tournament.startDate).toLocaleDateString()}</span>
                      </div>
                      <span className="tournament-prize">{tournament.prize}</span>
                    </div>
                  ))
                ) : (
                  <p className="no-tournaments">참여한 대회가 없습니다.</p>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyPage;