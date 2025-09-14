import React from 'react';
import GamerLoungeNavigation from '../components/GamerLoungeNavigation';
import './GamerLoungePage.css';

const GamerLoungePage: React.FC = () => {
  return (
    <div className="gamer-lounge">
      <GamerLoungeNavigation />

      <main className="lounge-content">
        <div className="welcome-section">
          <h2>환영합니다!</h2>
          <p>게이머 라운지에서 덱을 관리하고 대회에 참가해보세요.</p>
        </div>
        
        {/* 메인 컨텐츠 영역 - 현재 비어있음 */}
        <div className="main-content-area">
          {/* 추후 컨텐츠 추가 예정 */}
        </div>
      </main>
    </div>
  );
};

export default GamerLoungePage;