import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

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

// 카드 관련 API
export interface Card {
  id: number;
  name: string;
  type: string;
  rarity: string;
  color: string;
  cost?: number;
  attack?: number;
  defense?: number;
  description: string;
  imageUrl: string;
  setCode: string;
  cardNumber: string;
}

export interface CardSearchRequest {
  query?: string;
  type?: string;
  rarity?: string;
  color?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export const cardApi = {
  searchCards: async (
    params: CardSearchRequest,
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<Card>> => {
    const response = await api.get('/cards/search', {
      params: {
        ...params,
        page,
        size,
      },
    });
    return response.data;
  },

  getCard: async (id: number): Promise<Card> => {
    const response = await api.get(`/cards/${id}`);
    return response.data;
  },

  getFeaturedCards: async (): Promise<Card[]> => {
    const response = await api.get('/cards/featured');
    return response.data;
  },
};

export default api;