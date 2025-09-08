import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('tcg_lounge_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('tcg_lounge_token');
      localStorage.removeItem('tcg_lounge_user');
      
      // Don't redirect on login/signup pages
      if (!window.location.pathname.includes('/auth')) {
        window.location.href = '/';
      }
    }
    return Promise.reject(error);
  }
);

// 카드 관련 API - 백엔드 Card 엔티티와 일치하도록 수정
export interface Card {
  cardId: number;
  cardName: string;
  cardImg?: string;
  description?: string;
  cardColor: 'RED' | 'BLUE' | 'GREEN' | 'YELLOW' | 'BLACK' | 'COLORLESS';
  rarity: 'COMMON' | 'RARE' | 'SUPER_RARE' | 'ULTRA_RARE' | 'SECRET_RARE' | 'LEGENDARY';
  cost?: number;
  cardNumber?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CardSearchRequest {
  name?: string;
  cardColor?: string;
  rarity?: string;
}

export interface CardCreateRequest {
  cardName: string;
  cardColor: 'RED' | 'BLUE' | 'GREEN' | 'YELLOW' | 'BLACK' | 'COLORLESS';
  rarity: 'COMMON' | 'RARE' | 'SUPER_RARE' | 'ULTRA_RARE' | 'SECRET_RARE' | 'LEGENDARY';
  cost?: number;
}

export const cardApi = {
  // 모든 카드 조회
  getAllCards: async (): Promise<Card[]> => {
    const response = await api.get('/v1/cards');
    return response.data;
  },

  // 카드명으로 검색
  searchCardsByName: async (name: string): Promise<Card[]> => {
    const response = await api.get('/v1/cards/search', {
      params: { name }
    });
    return response.data;
  },

  // 특정 카드 조회
  getCard: async (id: number): Promise<Card> => {
    const response = await api.get(`/v1/cards/${id}`);
    return response.data;
  },

  // 새 카드 생성
  createCard: async (cardData: CardCreateRequest): Promise<{success: boolean, message: string, cardId?: number}> => {
    const response = await api.post('/v1/cards', cardData);
    return response.data;
  },

  // 샘플 데이터 초기화
  initSampleData: async (): Promise<{success: boolean, message: string, cardsCreated?: number}> => {
    const response = await api.post('/v1/cards/init-sample-data');
    return response.data;
  },
};

export default api;