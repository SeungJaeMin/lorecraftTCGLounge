import React, { useState, useEffect } from 'react';
import ImageUpload from '../components/ImageUpload';

interface Card {
  card_id: number;
  card_name: string;
  card_color: string;
  rarity: string;
  cost: number;
}

const ImageUploadTest: React.FC = () => {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');

  // 테스트용 더미 카드 데이터 (DB에서 가져오는 대신)
  const dummyCards: Card[] = [
    { card_id: 1, card_name: '화염 드래곤', card_color: 'RED', rarity: 'LEGENDARY', cost: 8 },
    { card_id: 2, card_name: '빙결의 마법사', card_color: 'BLUE', rarity: 'RARE', cost: 5 },
    { card_id: 3, card_name: '번개 폭풍', card_color: 'YELLOW', rarity: 'COMMON', cost: 3 },
    { card_id: 4, card_name: '치유의 성수', card_color: 'COLORLESS', rarity: 'COMMON', cost: 2 },
  ];

  useEffect(() => {
    // 실제로는 API에서 카드 데이터를 가져오지만, 지금은 더미 데이터 사용
    const fetchCards = async () => {
      try {
        // const response = await fetch('http://localhost:8090/api/v1/cards');
        // if (response.ok) {
        //   const data = await response.json();
        //   setCards(data);
        // } else {
        //   throw new Error('카드 데이터를 가져오는데 실패했습니다');
        // }
        
        // 더미 데이터 사용
        setTimeout(() => {
          setCards(dummyCards);
          setLoading(false);
        }, 1000);
      } catch (err) {
        console.error('Error fetching cards:', err);
        setError('카드 데이터를 불러올 수 없습니다. 더미 데이터를 사용합니다.');
        setCards(dummyCards);
        setLoading(false);
      }
    };

    fetchCards();
  }, []);

  const createTestCard = async (cardData: any) => {
    try {
      const response = await fetch('http://localhost:8090/api/v1/cards', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(cardData),
      });

      if (response.ok) {
        const result = await response.json();
        alert(`카드 생성 성공: ${result.message}`);
        // 카드 목록 새로고침 로직 추가 가능
      } else {
        const errorText = await response.text();
        alert(`카드 생성 실패: ${response.status} - ${errorText}`);
      }
    } catch (error) {
      console.error('Error creating card:', error);
      alert(`카드 생성 에러: ${error}`);
    }
  };

  if (loading) {
    return <div className="loading">카드 데이터 로딩 중...</div>;
  }

  return (
    <div className="image-upload-test">
      <h1>🔥 암호화된 이미지 업로드 테스트</h1>
      
      {error && (
        <div className="error-message">
          ⚠️ {error}
        </div>
      )}

      <div className="test-info">
        <h2>📋 테스트 안내</h2>
        <ul>
          <li>백엔드: <code>http://localhost:8090/api</code></li>
          <li>프론트엔드: <code>http://localhost:3000</code></li>
          <li>이미지는 AES-256으로 암호화되어 서버에 저장됩니다</li>
          <li>testcard 폴더의 이미지를 사용해서 테스트해보세요</li>
        </ul>
      </div>

      <div className="quick-actions">
        <h3>🔧 빠른 액션</h3>
        <button 
          onClick={() => createTestCard({
            cardName: '테스트 카드 ' + Date.now(),
            cardColor: 'RED',
            rarity: 'COMMON',
            cost: 1
          })}
          className="action-button"
        >
          새 테스트 카드 생성
        </button>
      </div>

      <div className="cards-grid">
        {cards.map((card) => (
          <ImageUpload
            key={card.card_id}
            cardId={card.card_id}
            cardName={card.card_name}
          />
        ))}
      </div>

      <div className="test-links">
        <h3>🔗 테스트 링크</h3>
        <ul>
          {cards.map((card) => (
            <li key={card.card_id}>
              <a 
                href={`http://localhost:8090/api/v1/cards/${card.card_id}/images`}
                target="_blank"
                rel="noopener noreferrer"
              >
                카드 {card.card_id}의 모든 이미지 보기
              </a>
            </li>
          ))}
        </ul>
      </div>

      <style>{`
        .image-upload-test {
          padding: 20px;
          max-width: 1200px;
          margin: 0 auto;
          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .error-message {
          background-color: #fff3cd;
          color: #856404;
          padding: 12px;
          border-radius: 4px;
          margin-bottom: 20px;
          border-left: 4px solid #ffc107;
        }

        .test-info {
          background-color: #d4edda;
          padding: 15px;
          border-radius: 8px;
          margin-bottom: 20px;
          border-left: 4px solid #28a745;
        }

        .test-info ul {
          margin: 10px 0;
          padding-left: 20px;
        }

        .test-info code {
          background-color: #f8f9fa;
          padding: 2px 4px;
          border-radius: 3px;
          font-family: monospace;
        }

        .quick-actions {
          margin-bottom: 30px;
          padding: 15px;
          background-color: #f8f9fa;
          border-radius: 8px;
        }

        .action-button {
          background-color: #007bff;
          color: white;
          border: none;
          padding: 10px 15px;
          border-radius: 5px;
          cursor: pointer;
          font-size: 14px;
          transition: background-color 0.2s;
        }

        .action-button:hover {
          background-color: #0056b3;
        }

        .cards-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
          gap: 20px;
          margin-bottom: 30px;
        }

        .test-links {
          background-color: #e9ecef;
          padding: 15px;
          border-radius: 8px;
        }

        .test-links ul {
          list-style-type: none;
          padding: 0;
        }

        .test-links li {
          margin-bottom: 8px;
        }

        .test-links a {
          color: #007bff;
          text-decoration: none;
        }

        .test-links a:hover {
          text-decoration: underline;
        }

        .loading {
          text-align: center;
          padding: 50px;
          font-size: 18px;
        }

        :global(.image-upload-card) {
          background-color: white;
          border: 1px solid #ddd;
          border-radius: 8px;
          padding: 20px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        :global(.image-upload-card h3) {
          margin-top: 0;
          color: #333;
          border-bottom: 2px solid #f0f0f0;
          padding-bottom: 10px;
        }

        :global(.upload-form) {
          margin-bottom: 15px;
        }

        :global(.form-group) {
          margin-bottom: 15px;
        }

        :global(.form-group label) {
          display: block;
          margin-bottom: 5px;
          font-weight: bold;
          color: #555;
        }

        :global(.form-group input),
        :global(.form-group select) {
          width: 100%;
          padding: 8px 12px;
          border: 1px solid #ddd;
          border-radius: 4px;
          font-size: 14px;
          max-width: 300px;
        }

        :global(.upload-button) {
          background-color: #28a745;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 5px;
          cursor: pointer;
          font-size: 16px;
          transition: background-color 0.2s;
        }

        :global(.upload-button:hover:not(:disabled)) {
          background-color: #218838;
        }

        :global(.upload-button:disabled) {
          background-color: #6c757d;
          cursor: not-allowed;
        }

        :global(.result) {
          margin-top: 15px;
          padding: 10px;
          border-radius: 4px;
          border-left: 4px solid transparent;
        }

        :global(.result.success) {
          background-color: #d4edda;
          border-left-color: #28a745;
          color: #155724;
        }

        :global(.result.error) {
          background-color: #f8d7da;
          border-left-color: #dc3545;
          color: #721c24;
        }

        :global(.result pre) {
          margin: 0;
          white-space: pre-wrap;
          word-wrap: break-word;
          font-family: monospace;
          font-size: 12px;
        }
      `}</style>
    </div>
  );
};

export default ImageUploadTest;