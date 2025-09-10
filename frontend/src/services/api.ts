import axios from 'axios';

const API_BASE_URL = 'http://localhost:8090/api/v1';

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

// 카드 이미지 DTO
export interface CardImage {
  imageId: number;
  cardId: number;
  imageName?: string;
  imageType?: string;
  imageSize?: number;
  imageCategory?: string;
  width?: number;
  height?: number;
  uploadedAt?: string;
}

// 카드 관련 API - 백엔드 CardDTO와 일치하도록 수정
export interface Card {
  cardId: number;
  cardName: string;
  cardImg?: string;
  description?: string;
  cardColor: 'RED' | 'BLUE' | 'GREEN' | 'YELLOW' | 'BLACK' | 'COLORLESS';
  rarity: 'COMMON' | 'RARE' | 'SUPER_RARE' | 'ULTRA_RARE' | 'SECRET_RARE' | 'LEGENDARY';
  cost?: number;
  cardNumber?: string;
  burstSlot1?: number;
  burstSlot2?: number;
  burstSlot3?: number;
  burstValue?: number;
  lifePoints?: number;
  power?: number;
  createdAt?: string;
  updatedAt?: string;
  images?: CardImage[];  // 카드에 업로드된 이미지 목록
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
    const response = await api.get('/cards');
    return response.data;
  },

  // 카드명으로 검색
  searchCardsByName: async (name: string): Promise<Card[]> => {
    const response = await api.get('/cards/search', {
      params: { name }
    });
    return response.data;
  },

  // 특정 카드 조회
  getCard: async (id: number): Promise<Card> => {
    const response = await api.get(`/cards/${id}`);
    return response.data;
  },

  // 새 카드 생성
  createCard: async (cardData: CardCreateRequest): Promise<{success: boolean, message: string, cardId?: number}> => {
    const response = await api.post('/cards', cardData);
    return response.data;
  },

  // 샘플 데이터 초기화
  initSampleData: async (): Promise<{success: boolean, message: string, cardsCreated?: number}> => {
    const response = await api.post('/cards/init-sample-data');
    return response.data;
  },
};

// 인증 관련 API
export const authAPI = {
  // 로그인
  login: async (username: string, password: string) => {
    const response = await api.post('/auth/login', {
      username,
      password
    });
    return response;
  },

  // 로그아웃
  logout: async () => {
    const response = await api.post('/auth/logout');
    return response;
  },

  // 현재 사용자 정보 조회
  getCurrentUser: async () => {
    const response = await api.get('/auth/me');
    return response;
  },

  // 테스트 사용자 생성
  createTestUsers: async () => {
    const response = await api.post('/test/create-test-users');
    return response;
  }
};

// 게이머 관련 API
export const gamerAPI = {
  // 게이머 프로필 조회
  getProfile: async () => {
    const response = await api.get('/gamer/profile');
    return response;
  },

  // 게이머 대시보드 데이터 조회
  getDashboard: async () => {
    const response = await api.get('/gamer/dashboard');
    return response;
  }
};

export default api;