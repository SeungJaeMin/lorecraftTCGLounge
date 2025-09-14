import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { deckAPI, cardAPI, Card, Deck, DeckDetail, DeckStats } from '../services/api';
import './DeckEditor.css';

interface DeckCard extends DeckDetail {
  id: string;
}

const DeckEditor: React.FC = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const navigate = useNavigate();
  const isNewDeck = deckId === 'new';

  // Deck State
  const [deck, setDeck] = useState<Deck | null>(null);
  const [deckCards, setDeckCards] = useState<DeckCard[]>([]);
  const [deckStats, setDeckStats] = useState<DeckStats | null>(null);
  
  // UI State
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  
  // Deck Info State
  const [deckName, setDeckName] = useState('');
  const [deckDescription, setDeckDescription] = useState('');
  const [isPublic, setIsPublic] = useState(false);
  
  // Card Search State
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Card[]>([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [selectedCardType, setSelectedCardType] = useState<string>('ALL');

  useEffect(() => {
    if (isNewDeck) {
      setLoading(false);
      setDeckName('새로운 덱');
    } else {
      loadDeck();
    }
    loadAllCards(); // Load initial card list
  }, [deckId]);

  const loadDeck = async () => {
    try {
      setLoading(true);
      const response = await deckAPI.getDeck(parseInt(deckId!));
      if (response.data.success) {
        const { deck, cards, stats } = response.data;
        setDeck(deck);
        setDeckName(deck.deckName);
        setDeckDescription(deck.description || '');
        setIsPublic(deck.isPublic);
        setDeckCards(cards.map((card: DeckDetail, index: number) => ({
          ...card,
          id: `${card.card.cardId}-${index}`
        })));
        setDeckStats(stats);
      } else {
        setError(response.data.message);
      }
    } catch (error: any) {
      setError('덱을 불러오는 중 오류가 발생했습니다.');
      console.error('Error loading deck:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadAllCards = async () => {
    try {
      const response = await cardAPI.getAllCards();
      if (response.data.success) {
        setSearchResults(response.data.cards);
      }
    } catch (error) {
      console.error('Error loading cards:', error);
    }
  };

  const searchCards = async (query: string) => {
    if (!query.trim()) {
      loadAllCards();
      return;
    }

    try {
      setSearchLoading(true);
      const response = await cardAPI.searchCards(query);
      if (response.data.success) {
        setSearchResults(response.data.cards);
      }
    } catch (error) {
      console.error('Error searching cards:', error);
    } finally {
      setSearchLoading(false);
    }
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value;
    setSearchQuery(query);
    
    // Debounce search
    setTimeout(() => {
      if (query === searchQuery) {
        searchCards(query);
      }
    }, 300);
  };

  const saveDeck = async () => {
    try {
      setSaving(true);
      let deckToSave = deck;

      if (isNewDeck) {
        const response = await deckAPI.createDeck({
          deckName: deckName.trim() || '새로운 덱',
          description: deckDescription
        });
        if (response.data.success) {
          deckToSave = response.data.deck;
          setDeck(deckToSave);
          if (deckToSave?.id) {
            navigate(`/deck-editor/${deckToSave.id}`, { replace: true });
          }
        } else {
          throw new Error(response.data.message);
        }
      } else {
        const response = await deckAPI.updateDeck(parseInt(deckId!), {
          deckName: deckName.trim(),
          description: deckDescription,
          isPublic
        });
        if (response.data.success) {
          deckToSave = response.data.deck;
          setDeck(deckToSave);
        } else {
          throw new Error(response.data.message);
        }
      }

      // Update stats after save
      if (deckToSave?.id) {
        const statsResponse = await deckAPI.getDeckStats(deckToSave.id);
        if (statsResponse.data.success) {
          setDeckStats(statsResponse.data.stats);
        }
      }

      alert('덱이 저장되었습니다!');
    } catch (error: any) {
      console.error('Error saving deck:', error);
      alert(error.message || '덱 저장 중 오류가 발생했습니다.');
    } finally {
      setSaving(false);
    }
  };

  const addCardToDeck = async (card: Card) => {
    if (!deck && isNewDeck) {
      alert('먼저 덱을 저장해주세요.');
      return;
    }

    try {
      const targetDeckId = deck?.id || parseInt(deckId!);
      const response = await deckAPI.addCardToDeck(targetDeckId, {
        cardId: card.cardId,
        quantity: 1
      });

      if (response.data.success) {
        // Update local deck cards
        const existingCardIndex = deckCards.findIndex(dc => dc.card.cardId === card.cardId);
        
        if (existingCardIndex >= 0) {
          const updatedCards = [...deckCards];
          updatedCards[existingCardIndex] = {
            ...updatedCards[existingCardIndex],
            quantity: updatedCards[existingCardIndex].quantity + 1
          };
          setDeckCards(updatedCards);
        } else {
          const newDeckCard: DeckCard = {
            id: `${card.cardId}-${Date.now()}`,
            deck: deck!,
            card,
            quantity: 1,
            isSideboard: false,
            orderIndex: deckCards.length + 1
          };
          setDeckCards([...deckCards, newDeckCard]);
        }

        // Update deck stats
        loadDeckStats();
      } else {
        alert(response.data.message || '카드 추가에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('Error adding card:', error);
      alert(error.message || '카드 추가 중 오류가 발생했습니다.');
    }
  };

  const removeCardFromDeck = async (card: Card) => {
    if (!deck) return;

    try {
      const response = await deckAPI.removeCardFromDeck(deck.id, {
        cardId: card.cardId,
        quantity: 1
      });

      if (response.data.success) {
        // Update local deck cards
        const existingCardIndex = deckCards.findIndex(dc => dc.card.cardId === card.cardId);
        
        if (existingCardIndex >= 0) {
          const updatedCards = [...deckCards];
          const currentQuantity = updatedCards[existingCardIndex].quantity;
          
          if (currentQuantity <= 1) {
            updatedCards.splice(existingCardIndex, 1);
          } else {
            updatedCards[existingCardIndex] = {
              ...updatedCards[existingCardIndex],
              quantity: currentQuantity - 1
            };
          }
          setDeckCards(updatedCards);
        }

        // Update deck stats
        loadDeckStats();
      } else {
        alert(response.data.message || '카드 제거에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('Error removing card:', error);
      alert('카드 제거 중 오류가 발생했습니다.');
    }
  };

  const loadDeckStats = async () => {
    if (!deck) return;
    
    try {
      const response = await deckAPI.getDeckStats(deck.id);
      if (response.data.success) {
        setDeckStats(response.data.stats);
      }
    } catch (error) {
      console.error('Error loading deck stats:', error);
    }
  };

  const generateRandomDeck = async () => {
    if (!window.confirm('랜덤 덱을 생성하면 기존 카드가 모두 교체됩니다. 계속하시겠습니까?')) {
      return;
    }

    try {
      setSaving(true);
      const response = await deckAPI.generateRandomDeck(deckName || '랜덤 덱');
      
      if (response.data.success) {
        const newDeck = response.data.deck;
        setDeck(newDeck);
        navigate(`/deck-editor/${newDeck.id}`, { replace: true });
        await loadDeck(); // Reload to get all cards
      } else {
        alert(response.data.message || '랜덤 덱 생성에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('Error generating random deck:', error);
      alert('랜덤 덱 생성 중 오류가 발생했습니다.');
    } finally {
      setSaving(false);
    }
  };

  const filteredSearchResults = searchResults.filter(card => {
    if (selectedCardType === 'ALL') return true;
    return card.cardType === selectedCardType;
  });

  const getTotalCards = () => {
    return deckCards.reduce((total, card) => total + card.quantity, 0);
  };

  const getCardTypeCount = (type: string) => {
    return deckCards
      .filter(card => card.card.cardType === type)
      .reduce((total, card) => total + card.quantity, 0);
  };

  const getCardInDeck = (cardId: number) => {
    return deckCards.find(dc => dc.card.cardId === cardId);
  };

  if (loading) {
    return (
      <div className="deck-editor-loading">
        <div className="loading-spinner"></div>
        <p>덱 에디터를 로딩 중...</p>
      </div>
    );
  }

  const totalCards = getTotalCards();
  const isComplete = totalCards >= 40;
  const leaderCount = getCardTypeCount('LEADER');
  const unitCount = getCardTypeCount('UNIT');
  const skillCount = getCardTypeCount('SKILL');
  const itemCount = getCardTypeCount('ITEM');
  const fieldCount = getCardTypeCount('FIELD');

  return (
    <div className="deck-editor">
      {/* Fixed Deck Parameters Header */}
      <div className="deck-params-header">
        <div className="deck-params-container">
          <div className="deck-info">
            <input
              type="text"
              value={deckName}
              onChange={(e) => setDeckName(e.target.value)}
              className="deck-name-input"
              placeholder="덱 이름"
            />
            <div className="deck-status">
              <span className={`completion-status ${isComplete ? 'complete' : 'incomplete'}`}>
                {isComplete ? '완성' : '미완성'} ({totalCards}/45)
              </span>
            </div>
          </div>
          
          <div className="card-type-counts">
            <div className="type-count leader">
              <span className="type-label">리더</span>
              <span className="count">{leaderCount}</span>
            </div>
            <div className="type-count unit">
              <span className="type-label">유닛</span>
              <span className="count">{unitCount}</span>
            </div>
            <div className="type-count skill">
              <span className="type-label">스킬</span>
              <span className="count">{skillCount}</span>
            </div>
            <div className="type-count item">
              <span className="type-label">아이템</span>
              <span className="count">{itemCount}</span>
            </div>
            <div className="type-count field">
              <span className="type-label">필드</span>
              <span className="count">{fieldCount}</span>
            </div>
          </div>

          <div className="deck-actions">
            <button 
              onClick={generateRandomDeck}
              className="random-deck-btn"
              disabled={saving}
            >
              랜덤 생성
            </button>
            <button 
              onClick={saveDeck}
              className="save-deck-btn"
              disabled={saving}
            >
              {saving ? '저장 중...' : '저장'}
            </button>
          </div>
        </div>
      </div>

      <div className="deck-editor-content">
        {/* Current Deck Section */}
        <div className="current-deck-section">
          <h2>현재 내 덱</h2>
          <div className="deck-cards-grid">
            {deckCards.length === 0 ? (
              <div className="empty-deck">
                <p>덱이 비어있습니다. 아래에서 카드를 선택해서 추가해보세요!</p>
              </div>
            ) : (
              deckCards.map(deckCard => (
                <div key={deckCard.id} className="deck-card-item">
                  <div className="card-image-placeholder">
                    <span className="card-type-badge">{deckCard.card.cardType}</span>
                  </div>
                  <div className="card-info">
                    <h4 className="card-name">{deckCard.card.cardName}</h4>
                    <p className="card-details">
                      {deckCard.card.cardColor} | {deckCard.card.manaCost}코스트
                    </p>
                    <div className="card-quantity">
                      <button 
                        onClick={() => removeCardFromDeck(deckCard.card)}
                        className="quantity-btn minus"
                      >
                        -
                      </button>
                      <span className="quantity">{deckCard.quantity}</span>
                      <button 
                        onClick={() => addCardToDeck(deckCard.card)}
                        className="quantity-btn plus"
                        disabled={deckCard.quantity >= 3 || (deckCard.card.cardType === 'LEADER' && deckCard.quantity >= 1)}
                      >
                        +
                      </button>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Card Search Section */}
        <div className="card-search-section">
          <h2>카드 검색</h2>
          <div className="search-controls">
            <div className="search-input-group">
              <input
                type="text"
                value={searchQuery}
                onChange={handleSearchChange}
                placeholder="카드 이름으로 검색..."
                className="card-search-input"
              />
              {searchLoading && <div className="search-loading">검색 중...</div>}
            </div>
            <select
              value={selectedCardType}
              onChange={(e) => setSelectedCardType(e.target.value)}
              className="card-type-filter"
            >
              <option value="ALL">모든 타입</option>
              <option value="LEADER">리더</option>
              <option value="UNIT">유닛</option>
              <option value="SKILL">스킬</option>
              <option value="ITEM">아이템</option>
              <option value="FIELD">필드</option>
            </select>
          </div>
        </div>

        {/* Card Search Results */}
        <div className="card-results-section">
          <h3>카드 검색 결과 ({filteredSearchResults.length})</h3>
          <div className="search-results-grid">
            {filteredSearchResults.map(card => {
              const deckCard = getCardInDeck(card.cardId);
              const canAdd = !deckCard || (deckCard.quantity < 3 && !(card.cardType === 'LEADER' && deckCard.quantity >= 1));
              
              return (
                <div key={card.cardId} className="search-result-card">
                  <div className="card-image-placeholder">
                    <span className="card-type-badge">{card.cardType}</span>
                  </div>
                  <div className="card-info">
                    <h4 className="card-name">{card.cardName}</h4>
                    <p className="card-details">
                      {card.cardColor} | {card.manaCost}코스트
                    </p>
                    <p className="card-description">{card.cardDescription}</p>
                    <div className="card-actions">
                      <button
                        onClick={() => addCardToDeck(card)}
                        className={`add-card-btn ${canAdd ? 'enabled' : 'disabled'}`}
                        disabled={!canAdd}
                      >
                        {deckCard ? `덱에 추가 (${deckCard.quantity})` : '덱에 추가'}
                      </button>
                      {deckCard && (
                        <button
                          onClick={() => removeCardFromDeck(card)}
                          className="remove-card-btn"
                        >
                          제거
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeckEditor;