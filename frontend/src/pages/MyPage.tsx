import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { gamerAPI } from '../services/api';
import GamerLoungeNavigation from '../components/GamerLoungeNavigation';
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

  // 랭크 결정 함수
  const getRankInfo = (rating: number) => {
    if (rating >= 2000) return { rank: 'LEGEND', name: '전설', color: '#ff6b35', icon: '👑' };
    if (rating >= 1500) return { rank: 'DIAMOND', name: '다이아몬드', color: '#00d4ff', icon: '💎' };
    if (rating >= 1200) return { rank: 'GOLD', name: '골드', color: '#ffd700', icon: '🥇' };
    if (rating >= 800) return { rank: 'SILVER', name: '실버', color: '#c0c0c0', icon: '🥈' };
    return { rank: 'BRONZE', name: '브론즈', color: '#cd7f32', icon: '🥉' };
  };

  const rankInfo = userProfile ? getRankInfo(userProfile.currentRating) : null;

  return (
    <div className="mypage">
      <GamerLoungeNavigation />
      <div className="mypage-container">
        <h1 className="mypage-title">마이페이지</h1>

        {/* 랭크 정보 섹션 */}
        <div className="rank-section">
          <div className="rank-card">
            <div className="rank-header">
              <div className="rank-icon-container">
                <span className="rank-icon">{rankInfo?.icon}</span>
              </div>
              <div className="rank-info">
                <h2 className="rank-title" style={{ color: rankInfo?.color }}>
                  {rankInfo?.name}
                </h2>
                <p className="rating-points">{userProfile?.currentRating || 0} RP</p>
              </div>
              <div className="rank-progress">
                <div className="progress-bar">
                  <div 
                    className="progress-fill" 
                    style={{ 
                      width: `${Math.min(((userProfile?.currentRating || 0) % 300) / 300 * 100, 100)}%`,
                      background: rankInfo?.color 
                    }}
                  ></div>
                </div>
                <span className="progress-text">다음 랭크까지</span>
              </div>
            </div>
            
            <div className="player-stats">
              <div className="player-info">
                <div className="info-card">
                  <h3>플레이어 정보</h3>
                  <div className="info-grid">
                    <div className="info-item">
                      <span className="info-label">닉네임</span>
                      <span className="info-value">{userProfile?.nickname}</span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">이메일</span>
                      <span className="info-value">{userProfile?.email}</span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">보유 포인트</span>
                      <span className="info-value points">{userProfile?.usablePoint?.toLocaleString() || 0} P</span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="battle-stats">
                <div className="stats-card">
                  <h3>전투 통계</h3>
                  <div className="stats-grid">
                    <div className="stat-circle wins">
                      <div className="stat-number">{userProfile?.totalWins || 0}</div>
                      <div className="stat-label">승리</div>
                    </div>
                    <div className="stat-circle losses">
                      <div className="stat-number">{userProfile?.totalLosses || 0}</div>
                      <div className="stat-label">패배</div>
                    </div>
                    <div className="stat-circle draws">
                      <div className="stat-number">{userProfile?.totalDraws || 0}</div>
                      <div className="stat-label">무승부</div>
                    </div>
                  </div>
                  <div className="winrate-display">
                    <div className="winrate-circle">
                      <span className="winrate-percentage">{Math.round(userProfile?.winRate || 0)}%</span>
                      <span className="winrate-label">승률</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
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