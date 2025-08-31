import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

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