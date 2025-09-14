import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { deckAPI } from '../services/api';
import GamerLoungeNavigation from '../components/GamerLoungeNavigation';
import './MyDecks.css';

interface Deck {
  id: number;
  deckName: string;
  description: string;
  totalCards: number;
  isPublic: boolean;
  isTournamentLegal: boolean;
  leaderCard?: {
    cardId: number;
    cardName: string;
    cardColor: string;
    cardType: string;
  };
  likesCount: number;
  viewsCount: number;
  updatedAt: string;
  createdAt: string;
}

const MyDecks: React.FC = () => {
  const navigate = useNavigate();
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [sortBy, setSortBy] = useState<'name' | 'updated' | 'cards'>('updated');
  const [filterBy, setFilterBy] = useState<'all' | 'complete' | 'incomplete'>('all');

  useEffect(() => {
    fetchMyDecks();
  }, []);

  const fetchMyDecks = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }

      const response = await deckAPI.getMyDecks();
      if (response.data.success) {
        setDecks(response.data.decks);
      } else {
        setError(response.data.message || 'Failed to fetch decks');
      }
    } catch (error: any) {
      console.error('Error fetching decks:', error);
      setError('Failed to load decks');
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

  const handleDeleteDeck = async (deckId: number, deckName: string) => {
    if (!window.confirm(`"${deckName}" 덱을 정말 삭제하시겠습니까?`)) {
      return;
    }

    try {
      const response = await deckAPI.deleteDeck(deckId);
      if (response.data.success) {
        setDecks(decks.filter(deck => deck.id !== deckId));
      } else {
        alert(response.data.message || 'Failed to delete deck');
      }
    } catch (error) {
      console.error('Error deleting deck:', error);
      alert('덱 삭제 중 오류가 발생했습니다.');
    }
  };

  const sortedAndFilteredDecks = decks
    .filter(deck => {
      if (filterBy === 'complete') return deck.totalCards >= 40;
      if (filterBy === 'incomplete') return deck.totalCards < 40;
      return true;
    })
    .sort((a, b) => {
      switch (sortBy) {
        case 'name':
          return a.deckName.localeCompare(b.deckName);
        case 'cards':
          return b.totalCards - a.totalCards;
        case 'updated':
        default:
          return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
      }
    });

  const getCardColorClass = (color: string) => {
    return color ? color.toLowerCase() : 'colorless';
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="mydecks-loading">
        <div className="loading-spinner"></div>
        <p>덱 목록을 불러오는 중...</p>
      </div>
    );
  }

  return (
    <div className="mydecks">
      <GamerLoungeNavigation />
      <div className="mydecks-container">
        <div className="mydecks-header">
          <h1>내 덱 목록</h1>
          <button className="create-deck-button" onClick={handleCreateDeck}>
            새 덱 만들기
          </button>
        </div>

        {error && (
          <div className="error-message">
            <p>{error}</p>
            <button onClick={fetchMyDecks}>다시 시도</button>
          </div>
        )}

        <div className="mydecks-controls">
          <div className="filter-controls">
            <label>필터:</label>
            <select value={filterBy} onChange={(e) => setFilterBy(e.target.value as any)}>
              <option value="all">모든 덱</option>
              <option value="complete">완성된 덱 (40장+)</option>
              <option value="incomplete">미완성 덱</option>
            </select>
          </div>

          <div className="sort-controls">
            <label>정렬:</label>
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value as any)}>
              <option value="updated">최근 수정순</option>
              <option value="name">이름순</option>
              <option value="cards">카드 수</option>
            </select>
          </div>
        </div>

        <div className="deck-stats">
          <div className="stat-item">
            <span className="stat-number">{decks.length}</span>
            <span className="stat-label">총 덱</span>
          </div>
          <div className="stat-item">
            <span className="stat-number">{decks.filter(d => d.totalCards >= 40).length}</span>
            <span className="stat-label">완성된 덱</span>
          </div>
          <div className="stat-item">
            <span className="stat-number">{decks.filter(d => d.isPublic).length}</span>
            <span className="stat-label">공개된 덱</span>
          </div>
        </div>

        {sortedAndFilteredDecks.length === 0 ? (
          <div className="no-decks">
            <div className="no-decks-icon">🃏</div>
            <h3>덱이 없습니다</h3>
            <p>새로운 덱을 만들어 TCG를 시작해보세요!</p>
            <button className="create-first-deck" onClick={handleCreateDeck}>
              첫 번째 덱 만들기
            </button>
          </div>
        ) : (
          <div className="decks-grid">
            {sortedAndFilteredDecks.map(deck => (
              <div key={deck.id} className="deck-card">
                <div className="deck-card-header">
                  <div className="deck-title-section">
                    <h3 className="deck-name" title={deck.deckName}>
                      {deck.deckName}
                    </h3>
                    <div className="deck-badges">
                      {deck.totalCards >= 40 ? (
                        <span className="badge complete">완성</span>
                      ) : (
                        <span className="badge incomplete">미완성</span>
                      )}
                      {deck.isPublic && <span className="badge public">공개</span>}
                      {deck.isTournamentLegal && <span className="badge tournament">대회용</span>}
                    </div>
                  </div>
                  <div className="deck-actions">
                    <button 
                      className="action-button edit"
                      onClick={() => handleDeckClick(deck.id)}
                      title="덱 편집"
                    >
                      ✏️
                    </button>
                    <button 
                      className="action-button delete"
                      onClick={() => handleDeleteDeck(deck.id, deck.deckName)}
                      title="덱 삭제"
                    >
                      🗑️
                    </button>
                  </div>
                </div>

                <div className="deck-card-content">
                  {deck.leaderCard && (
                    <div className="leader-card-info">
                      <div className={`leader-icon ${getCardColorClass(deck.leaderCard.cardColor)}`}>
                        👑
                      </div>
                      <span className="leader-name">{deck.leaderCard.cardName}</span>
                    </div>
                  )}

                  <div className="deck-description">
                    {deck.description || '설명이 없습니다.'}
                  </div>

                  <div className="deck-stats-row">
                    <div className="stat">
                      <span className="stat-icon">🃏</span>
                      <span>{deck.totalCards}/45</span>
                    </div>
                    <div className="stat">
                      <span className="stat-icon">👁️</span>
                      <span>{deck.viewsCount}</span>
                    </div>
                    <div className="stat">
                      <span className="stat-icon">❤️</span>
                      <span>{deck.likesCount}</span>
                    </div>
                  </div>

                  <div className="deck-progress">
                    <div className="progress-bar">
                      <div 
                        className="progress-fill"
                        style={{ width: `${Math.min((deck.totalCards / 45) * 100, 100)}%` }}
                      ></div>
                    </div>
                    <span className="progress-text">
                      {Math.round((deck.totalCards / 45) * 100)}% 완성
                    </span>
                  </div>
                </div>

                <div className="deck-card-footer">
                  <span className="update-date">
                    최근 수정: {formatDate(deck.updatedAt)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default MyDecks;