import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { deckAPI, cardAPI, Card, Deck, DeckDetail, DeckStats } from '../services/api';
import GamerLoungeNavigation from '../components/GamerLoungeNavigation';
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
  const [originalDeckCards, setOriginalDeckCards] = useState<DeckCard[]>([]); // Track original state for saves
  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);
  const [deckStats, setDeckStats] = useState<DeckStats | null>(null);
  
  // UI State
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [currentDeckTab, setCurrentDeckTab] = useState<'images' | 'text'>('images');
  
  // Deck Info State
  const [deckName, setDeckName] = useState('');
  const [deckDescription, setDeckDescription] = useState('');
  const [isPublic, setIsPublic] = useState(false);
  const [selectedColor, setSelectedColor] = useState<string>('ALL');
  
  // Card Search State
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Card[]>([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [selectedCardType, setSelectedCardType] = useState<string>('ALL');
  const [selectedRarity, setSelectedRarity] = useState<string>('ALL');
  const [selectedCost, setSelectedCost] = useState<string>('ALL');
  const [filtersOpen, setFiltersOpen] = useState(false);

  useEffect(() => {
    if (isNewDeck) {
      setLoading(false);
      setDeckName('새로운 덱');
    } else {
      loadDeck();
    }
    searchCards(''); // Load initial card list
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
        const mappedCards = cards.map((card: DeckDetail, index: number) => ({
          ...card,
          id: `${card.card.cardId}-${index}`
        }));
        setDeckCards(mappedCards);
        setOriginalDeckCards(mappedCards);
        setDeckStats(stats);
      } else {
        setError(response.data.message);
      }
    } catch (error: any) {
      setError('덱을 불러오는 중 오류가 발생했습니다.');
      console.error('Error loading deck:', error);
      // If deck not found, redirect to new deck creation
      if (error.response?.status === 404 || error.response?.data?.message?.includes('not found')) {
        navigate('/deck-editor', { replace: true });
      }
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

  const searchCards = async (query: string = searchQuery) => {
    try {
      setSearchLoading(true);
      let results: Card[] = [];
      
      if (!query.trim()) {
        const response = await cardAPI.getAllCards();
        if (response.data.success) {
          results = response.data.cards;
        }
      } else {
        const response = await cardAPI.searchCards(query);
        if (response.data.success) {
          results = response.data.cards;
        }
      }
      
      setSearchResults(results);
    } catch (error) {
      console.error('Error searching cards:', error);
    } finally {
      setSearchLoading(false);
    }
  };
  
  const handleSearchSubmit = () => {
    searchCards(searchQuery);
  };
  
  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearchSubmit();
    }
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value;
    setSearchQuery(query);
  };

  const saveDeck = async () => {
    try {
      setSaving(true);
      
      // Use save with cards endpoint to include deck cards
      const response = await deckAPI.saveDeckWithCards({
        deckId: deckId ? parseInt(deckId) : undefined,
        deckName: deckName.trim() || '새로운 덱',
        description: deckDescription,
        isPublic,
        cards: deckCards
      });
      
      if (!response.data.success) {
        throw new Error(response.data.message);
      }
      
      const deckToSave = response.data.deck;
      setDeck(deckToSave);
      
      // Navigate to the new deck ID if it was just created
      if (!deckId && deckToSave?.id) {
        navigate(`/deck-editor/${deckToSave.id}`, { replace: true });
      }

      // Now save all the card changes if there are any
      if (hasUnsavedChanges && deckToSave?.id) {
        // Calculate what cards need to be added/removed
        const originalCardMap = new Map<number, number>();
        originalDeckCards.forEach(card => {
          originalCardMap.set(card.card.cardId, card.quantity);
        });
        
        const currentCardMap = new Map<number, number>();
        deckCards.forEach(card => {
          currentCardMap.set(card.card.cardId, card.quantity);
        });
        
        // Process each card change
        const originalCardIds = Array.from(originalCardMap.keys());
        const currentCardIds = Array.from(currentCardMap.keys());
        const allCardIds = Array.from(new Set([...originalCardIds, ...currentCardIds]));
        
        for (let i = 0; i < allCardIds.length; i++) {
          const cardId = allCardIds[i];
          const originalQuantity = originalCardMap.get(cardId) || 0;
          const currentQuantity = currentCardMap.get(cardId) || 0;
          const difference = currentQuantity - originalQuantity;
          
          if (difference > 0) {
            // Add cards
            await deckAPI.addCardToDeck(deckToSave.id, {
              cardId: cardId,
              quantity: difference
            });
          } else if (difference < 0) {
            // Remove cards
            await deckAPI.removeCardFromDeck(deckToSave.id, {
              cardId: cardId,
              quantity: Math.abs(difference)
            });
          }
        }
        
        // Update original state to match current state
        setOriginalDeckCards([...deckCards]);
        setHasUnsavedChanges(false);
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

  const addCardToDeck = (card: Card) => {
    const existingCardIndex = deckCards.findIndex(dc => dc.card.cardId === card.cardId);
    
    if (existingCardIndex >= 0) {
      const updatedCards = [...deckCards];
      const currentQuantity = updatedCards[existingCardIndex].quantity;
      
      // Check limits: max 3 per card, max 1 for LEADER cards
      if (currentQuantity >= 3 || (card.cardType === 'LEADER' && currentQuantity >= 1)) {
        alert(card.cardType === 'LEADER' ? '리더 카드는 1장만 추가할 수 있습니다.' : '같은 카드는 최대 3장까지만 추가할 수 있습니다.');
        return;
      }
      
      updatedCards[existingCardIndex] = {
        ...updatedCards[existingCardIndex],
        quantity: currentQuantity + 1
      };
      setDeckCards(updatedCards);
    } else {
      const newDeckCard: DeckCard = {
        id: `${card.cardId}-${Date.now()}`,
        deck: deck || { id: 0 } as Deck, // Temporary deck placeholder for new decks
        card,
        quantity: 1,
        isSideboard: false,
        orderIndex: deckCards.length + 1
      };
      setDeckCards([...deckCards, newDeckCard]);
    }
    
    setHasUnsavedChanges(true);
  };

  const removeCardFromDeck = (card: Card) => {
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
      setHasUnsavedChanges(true);
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
    const matchesType = selectedCardType === 'ALL' || card.cardType === selectedCardType;
    const matchesRarity = selectedRarity === 'ALL' || card.rarity === selectedRarity;
    const matchesCost = selectedCost === 'ALL' || 
      (selectedCost === '0-3' && (card.cost || card.manaCost || 0) <= 3) ||
      (selectedCost === '4-6' && (card.cost || card.manaCost || 0) >= 4 && (card.cost || card.manaCost || 0) <= 6) ||
      (selectedCost === '7+' && (card.cost || card.manaCost || 0) >= 7);
    
    return matchesType && matchesRarity && matchesCost;
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
      <GamerLoungeNavigation />
      
      {/* Row 1: Fixed Deck Parameters Header */}
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

          <div className="deck-controls">
            <select
              value={selectedColor}
              onChange={(e) => setSelectedColor(e.target.value)}
              className="color-filter"
            >
              <option value="ALL">모든 색상</option>
              <option value="RED">빨강</option>
              <option value="BLUE">파랑</option>
              <option value="GREEN">초록</option>
              <option value="YELLOW">노랑</option>
              <option value="BLACK">검정</option>
              <option value="COLORLESS">무색</option>
            </select>
            <button 
              onClick={generateRandomDeck}
              className="random-deck-btn"
              disabled={saving}
            >
              랜덤 생성
            </button>
            <button 
              onClick={saveDeck}
              className={`save-deck-btn ${hasUnsavedChanges ? 'has-changes' : ''}`}
              disabled={saving}
            >
              {saving ? '저장 중...' : hasUnsavedChanges ? '저장*' : '저장'}
            </button>
            <button 
              onClick={() => navigate('/my-decks')}
              className="load-deck-btn"
            >
              불러오기
            </button>
          </div>
        </div>
      </div>

      <div className="deck-editor-content">
        {/* Row 2: Current Deck Section with Tabs */}
        <div className="current-deck-section">
          <div className="deck-section-header">
            <h2>현재 내 덱</h2>
            <div className="deck-view-tabs">
              <button 
                className={`tab-button ${currentDeckTab === 'images' ? 'active' : ''}`}
                onClick={() => setCurrentDeckTab('images')}
              >
                이미지 표시
              </button>
              <button 
                className={`tab-button ${currentDeckTab === 'text' ? 'active' : ''}`}
                onClick={() => setCurrentDeckTab('text')}
              >
                텍스트 표시
              </button>
            </div>
          </div>
          
          <div className="deck-content">
            {currentDeckTab === 'images' ? (
              <div className="deck-cards-grid">
                {deckCards.length === 0 ? (
                  <div className="empty-deck">
                    <p>덱이 비어있습니다. 아래에서 카드를 선택해서 추가해보세요!</p>
                  </div>
                ) : (
                  deckCards.map(deckCard => (
                    <div key={deckCard.id} className="deck-card-item image-view">
                      <div className="card-image-placeholder">
                        <span className="card-type-badge">{deckCard.card.cardType}</span>
                        <span className="card-name-overlay">{deckCard.card.cardName}</span>
                        <div className="quantity-overlay">{deckCard.quantity}</div>
                      </div>
                      <div className="card-controls">
                        <button 
                          onClick={() => removeCardFromDeck(deckCard.card)}
                          className="quantity-btn minus"
                        >
                          -
                        </button>
                        <button 
                          onClick={() => addCardToDeck(deckCard.card)}
                          className="quantity-btn plus"
                          disabled={deckCard.quantity >= 3 || (deckCard.card.cardType === 'LEADER' && deckCard.quantity >= 1)}
                        >
                          +
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            ) : (
              <div className="deck-text-view">
                {['LEADER', 'UNIT', 'SKILL', 'ITEM', 'FIELD'].map(cardType => {
                  const cardsOfType = deckCards.filter(dc => dc.card.cardType === cardType);
                  if (cardsOfType.length === 0) return null;
                  
                  return (
                    <div key={cardType} className="card-type-section">
                      <h4 className="type-header">{cardType} ({cardsOfType.reduce((sum, card) => sum + card.quantity, 0)})</h4>
                      <div className="card-list">
                        {cardsOfType.map(deckCard => (
                          <div key={deckCard.id} className="text-card-item">
                            <span className="card-name">{deckCard.card.cardName}</span>
                            <span className="card-cost">{deckCard.card.cost || deckCard.card.manaCost}코스트</span>
                            <span className="card-color">{deckCard.card.cardColor}</span>
                            <div className="quantity-controls">
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
                        ))}
                      </div>
                    </div>
                  );
                })}
                {deckCards.length === 0 && (
                  <div className="empty-deck">
                    <p>덱이 비어있습니다. 아래에서 카드를 선택해서 추가해보세요!</p>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Row 3: Card Search Section */}
        <div className="card-search-section">
          <div className="search-header">
            <h2>카드 검색</h2>
            {searchLoading && <div className="search-loading">검색 중...</div>}
          </div>
          <div className="search-controls">
            <div className="search-input-container">
              <input
                type="text"
                value={searchQuery}
                onChange={handleSearchChange}
                onKeyPress={handleKeyPress}
                placeholder="카드 이름으로 검색..."
                className="card-search-input"
              />
              <button 
                onClick={handleSearchSubmit}
                className="search-button"
                disabled={searchLoading}
              >
                🔍
              </button>
            </div>
            <button 
              onClick={() => setFiltersOpen(!filtersOpen)}
              className="filters-toggle-button"
            >
              필터 {filtersOpen ? '▲' : '▼'}
            </button>
          </div>
          
          <div className={`filters-drawer ${filtersOpen ? 'open' : 'closed'}`}>
            <div className="filter-row">
              <div className="filter-group">
                <label>카드 타입</label>
                <select
                  value={selectedCardType}
                  onChange={(e) => setSelectedCardType(e.target.value)}
                  className="filter-select"
                >
                  <option value="ALL">모든 타입</option>
                  <option value="LEADER">리더</option>
                  <option value="UNIT">유닛</option>
                  <option value="SKILL">스킬</option>
                  <option value="ITEM">아이템</option>
                  <option value="FIELD">필드</option>
                </select>
              </div>
              
              <div className="filter-group">
                <label>희귀도</label>
                <select
                  value={selectedRarity}
                  onChange={(e) => setSelectedRarity(e.target.value)}
                  className="filter-select"
                >
                  <option value="ALL">모든 희귀도</option>
                  <option value="COMMON">커먼</option>
                  <option value="RARE">레어</option>
                  <option value="SUPER_RARE">슈퍼레어</option>
                  <option value="ULTRA_RARE">울트라레어</option>
                  <option value="SECRET_RARE">시크릿레어</option>
                  <option value="LEGENDARY">레전더리</option>
                </select>
              </div>
              
              <div className="filter-group">
                <label>코스트</label>
                <select
                  value={selectedCost}
                  onChange={(e) => setSelectedCost(e.target.value)}
                  className="filter-select"
                >
                  <option value="ALL">모든 코스트</option>
                  <option value="0-3">0-3</option>
                  <option value="4-6">4-6</option>
                  <option value="7+">7+</option>
                </select>
              </div>
              
              <div className="filter-group">
                <button 
                  onClick={() => {
                    setSelectedCardType('ALL');
                    setSelectedRarity('ALL');
                    setSelectedCost('ALL');
                    setSearchQuery('');
                    loadAllCards();
                  }}
                  className="clear-filters-button"
                >
                  필터 초기화
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Row 4: Card Search Results */}
        <div className="card-results-section">
          <div className="results-header">
            <h3>카드 검색 결과</h3>
            <span className="results-count">({filteredSearchResults.length}개)</span>
          </div>
          <div className="search-results-grid">
            {filteredSearchResults.map(card => {
              const deckCard = getCardInDeck(card.cardId);
              const canAdd = !deckCard || (deckCard.quantity < 3 && !(card.cardType === 'LEADER' && deckCard.quantity >= 1));
              
              return (
                <div key={card.cardId} className="search-result-card">
                  <div className="card-image-placeholder">
                    <span className="card-type-badge">{card.cardType}</span>
                    {deckCard && <div className="in-deck-indicator">{deckCard.quantity}</div>}
                  </div>
                  <div className="card-info">
                    <h4 className="card-name">{card.cardName}</h4>
                    <p className="card-details">
                      <span className="card-color">{card.cardColor}</span>
                      <span className="card-cost">{card.cost || card.manaCost}코스트</span>
                      <span className="card-rarity">{card.rarity}</span>
                    </p>
                    <p className="card-description">{card.description || card.cardDescription}</p>
                    <div className="card-actions">
                      <button
                        onClick={() => addCardToDeck(card)}
                        className={`add-card-btn ${canAdd ? 'enabled' : 'disabled'}`}
                        disabled={!canAdd}
                      >
                        {deckCard ? `추가 (${deckCard.quantity})` : '덱에 추가'}
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