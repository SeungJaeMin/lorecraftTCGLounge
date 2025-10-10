import axios from 'axios';

const API_BASE_URL = 'http://localhost:8090/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Enhanced token management
interface TokenInfo {
  token: string;
  expiresAt: number;
  refreshToken?: string;
}

class TokenManager {
  private static readonly TOKEN_KEY = 'tcg_lounge_token';
  private static readonly USER_KEY = 'tcg_lounge_user';
  private static readonly TOKEN_INFO_KEY = 'tcg_lounge_token_info';
  
  static setToken(token: string, expiresIn?: number): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    
    if (expiresIn) {
      const tokenInfo: TokenInfo = {
        token,
        expiresAt: Date.now() + (expiresIn * 1000) // Convert to milliseconds
      };
      localStorage.setItem(this.TOKEN_INFO_KEY, JSON.stringify(tokenInfo));
    }
  }
  
  static getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
  
  static isTokenExpired(): boolean {
    const tokenInfo = this.getTokenInfo();
    if (!tokenInfo || !tokenInfo.expiresAt) {
      return false; // No expiration info, assume valid
    }
    
    // Check if token expires within the next 5 minutes (for refresh)
    return Date.now() >= (tokenInfo.expiresAt - 5 * 60 * 1000);
  }
  
  static getTokenInfo(): TokenInfo | null {
    const info = localStorage.getItem(this.TOKEN_INFO_KEY);
    return info ? JSON.parse(info) : null;
  }
  
  static clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.TOKEN_INFO_KEY);
    localStorage.removeItem(this.USER_KEY);
  }
  
  static shouldRedirect(): boolean {
    const currentPath = window.location.pathname;
    const protectedPaths = ['/my-page', '/deck-editor', '/my-decks', '/gamer-lounge'];
    const isProtectedPath = protectedPaths.some(path => currentPath.includes(path));
    const isAuthPage = currentPath.includes('/auth') || currentPath === '/';
    
    return isProtectedPath && !isAuthPage;
  }
}

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = TokenManager.getToken();
    if (token) {
      // Check if token is about to expire
      if (TokenManager.isTokenExpired()) {
        console.warn('Token is about to expire. Consider implementing token refresh.');
        // In a production environment, you might want to refresh the token here
      }
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
      console.error('Authentication failed: Token expired or invalid');
      TokenManager.clearToken();
      
      // Only redirect if we're on a protected page
      if (TokenManager.shouldRedirect()) {
        // Show a more user-friendly message before redirecting
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
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
  cardDescription?: string;
  description?: string;
  cardColor: 'RED' | 'BLUE' | 'GREEN' | 'YELLOW' | 'BLACK' | 'COLORLESS';
  cardType: 'LEADER' | 'UNIT' | 'SKILL' | 'ITEM' | 'FIELD';
  rarity: 'COMMON' | 'RARE' | 'SUPER_RARE' | 'ULTRA_RARE' | 'SECRET_RARE' | 'LEGENDARY';
  manaCost?: number;
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

// DeckEditor에서 사용하는 cardAPI 별칭
export const cardAPI = {
  // 모든 카드 조회
  getAllCards: async () => {
    const response = await api.get('/cards');
    return {
      data: {
        success: true,
        cards: response.data
      }
    };
  },

  // 카드 검색
  searchCards: async (query: string) => {
    const response = await api.get('/cards/search', {
      params: { name: query }
    });
    return {
      data: {
        success: true,
        cards: response.data
      }
    };
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

// 사용자 관련 API
export const userAPI = {
  // 사용자 프로필 조회
  getUserProfile: async (userId: number) => {
    const response = await api.get(`/users/${userId}`);
    return response;
  },

  // 사용자 프로필 업데이트
  updateProfile: async (profileData: { nickname?: string; email?: string }) => {
    const response = await api.put('/users/profile', profileData);
    return response;
  },

  // 비밀번호 변경
  changePassword: async (passwordData: { currentPassword: string; newPassword: string }) => {
    const response = await api.put('/users/password', passwordData);
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

// Article 관련 인터페이스
export interface Article {
  id: number;
  title: string;
  content: string;
  summary?: string;
  author: string;
  authorId?: number;
  status: 'PUBLIC' | 'PRIVATE';
  category: 'NEWS' | 'ANNOUNCEMENT' | 'PRODUCT_INFO' | 'UPDATE' | 'EVENT';
  featured?: boolean;
  viewsCount?: number;
  likesCount?: number;
  thumbnailUrl?: string;
  metaTitle?: string;
  metaDescription?: string;
  createdAt: string;
  updatedAt: string;
  publishDate?: string;
  tags?: Tag[];
}

export interface Tag {
  id: number;
  name: string;
  slug: string;
  description?: string;
  color?: string;
  usageCount?: number;
  createdAt?: string;
}

export interface ArticleCreateRequest {
  title: string;
  content: string;
  summary?: string;
  author: string;
  authorId?: number;
  status: 'PUBLIC' | 'PRIVATE';
  category: 'NEWS' | 'ANNOUNCEMENT' | 'PRODUCT_INFO' | 'UPDATE' | 'EVENT';
  featured?: boolean;
  thumbnailUrl?: string;
  metaTitle?: string;
  metaDescription?: string;
  tagIds?: number[];
}

export interface TagCreateRequest {
  name: string;
  slug?: string;
  description?: string;
  color?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}

// Article API
export const articleApi = {
  // 게시글 목록 조회 (페이징)
  getArticles: async (params?: {
    page?: number;
    size?: number;
    status?: string;
    category?: string;
    search?: string;
  }): Promise<PageResponse<Article>> => {
    const response = await api.get('/articles', { params });
    return response.data;
  },

  // 특정 게시글 조회
  getArticle: async (id: number): Promise<Article> => {
    const response = await api.get(`/articles/${id}`);
    return response.data;
  },

  // 주요 게시글 조회
  getFeaturedArticles: async (): Promise<Article[]> => {
    const response = await api.get('/articles/featured');
    return response.data;
  },

  // 관리자 - 게시글 생성
  createArticle: async (data: ArticleCreateRequest): Promise<Article> => {
    const response = await api.post('/admin/articles', data);
    return response.data;
  },

  // 관리자 - 게시글 수정
  updateArticle: async (id: number, data: Partial<ArticleCreateRequest>): Promise<Article> => {
    const response = await api.put(`/admin/articles/${id}`, data);
    return response.data;
  },

  // 관리자 - 게시글 삭제
  deleteArticle: async (id: number): Promise<void> => {
    await api.delete(`/admin/articles/${id}`);
  },

  // 관리자 - 게시글 발행
  publishArticle: async (id: number): Promise<Article> => {
    const response = await api.post(`/admin/articles/${id}/publish`);
    return response.data;
  },

  // 관리자 - 게시글 보관
  archiveArticle: async (id: number): Promise<Article> => {
    const response = await api.post(`/admin/articles/${id}/archive`);
    return response.data;
  },

  // 관리자 - 주요 뉴스 토글
  toggleFeatured: async (id: number): Promise<Article> => {
    const response = await api.post(`/admin/articles/${id}/toggle-featured`);
    return response.data;
  },
};

// Tag API
export const tagApi = {
  // 태그 목록 조회
  getTags: async (): Promise<Tag[]> => {
    const response = await api.get('/tags');
    return response.data;
  },

  // 특정 태그 조회
  getTag: async (id: number): Promise<Tag> => {
    const response = await api.get(`/tags/${id}`);
    return response.data;
  },

  // 관리자 - 태그 생성
  createTag: async (data: TagCreateRequest): Promise<Tag> => {
    const response = await api.post('/admin/tags', data);
    return response.data;
  },

  // 관리자 - 태그 수정
  updateTag: async (id: number, data: Partial<TagCreateRequest>): Promise<Tag> => {
    const response = await api.put(`/admin/tags/${id}`, data);
    return response.data;
  },

  // 관리자 - 태그 삭제
  deleteTag: async (id: number): Promise<void> => {
    await api.delete(`/admin/tags/${id}`);
  },

  // 관리자 - 태그 사용 횟수 재계산
  recalculateUsage: async (id: number): Promise<Tag> => {
    const response = await api.post(`/admin/tags/${id}/recalculate-usage`);
    return response.data;
  },

  // 관리자 - 모든 태그 사용 횟수 재계산
  recalculateAllUsage: async (): Promise<{ message: string }> => {
    const response = await api.post('/admin/tags/recalculate-usage');
    return response.data;
  },
};

// Deck 관련 인터페이스
export interface Deck {
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
  deckCode: string;
  deckType: string;
}

export interface DeckDetail {
  deck: Deck;
  card: Card;
  quantity: number;
  isSideboard: boolean;
  orderIndex: number;
}

export interface DeckStats {
  totalCards: number;
  isComplete: boolean;
  typeDistribution: { [key: string]: number };
  colorDistribution: { [key: string]: number };
  leaderCard?: Card;
}

export interface DeckCreateRequest {
  deckName: string;
  description?: string;
}

export interface AddCardToDeckRequest {
  cardId: number;
  quantity: number;
}

export interface RemoveCardFromDeckRequest {
  cardId: number;
  quantity: number;
}

// Deck API
export const deckAPI = {
  // 내 덱 목록 조회
  getMyDecks: async () => {
    const response = await api.get('/decks/my-decks');
    return response;
  },

  // 특정 덱 조회
  getDeck: async (deckId: number) => {
    const  response = await api.get(`/decks/${deckId}`);
    return response;
  },

  // 새 덱 생성
  createDeck: async (deckData: DeckCreateRequest) => {
    const response = await api.post('/decks', deckData);
    return response;
  },

  // 덱 수정
  updateDeck: async (deckId: number, deckData: Partial<DeckCreateRequest & { isPublic?: boolean }>) => {
    const response = await api.put(`/decks/${deckId}`, deckData);
    return response;
  },
  
  // 통합 저장 (생성 또는 수정) - 카드와 함께
  saveDeck: async (deckData: {
    deckId?: number;
    deckName: string;
    description?: string;
    deckType?: string;
    isPublic?: boolean;
    isTournamentLegal?: boolean;
    leaderCardId?: number;
    cards?: Array<{
      cardId: number;
      quantity: number;
      isSideboard?: boolean;
      orderIndex?: number;
    }>;
  }) => {
    const response = await api.post('/decks/save', deckData);
    return response;
  },

  // 하위 호환성을 위한 별칭
  saveDeckWithCards: async (deckData: {
    deckId?: number;
    deckName: string;
    description?: string;
    isPublic?: boolean;
    cards: any[];
  }) => {
    // DeckDetail 형태의 cards를 백엔드 DTO 형태로 변환
    const transformedCards = deckData.cards.map((deckCard: any, index: number) => {
      // DeckCard가 DeckDetail 형태인 경우
      if (deckCard.card && deckCard.card.cardId) {
        return {
          cardId: deckCard.card.cardId,
          quantity: deckCard.quantity || 1,
          isSideboard: deckCard.isSideboard || false,
          orderIndex: deckCard.orderIndex || index + 1
        };
      }
      // 이미 올바른 형태인 경우
      return {
        cardId: deckCard.cardId,
        quantity: deckCard.quantity || 1,
        isSideboard: deckCard.isSideboard || false,
        orderIndex: deckCard.orderIndex || index + 1
      };
    });

    // 새로운 형식으로 변환
    const newFormat = {
      deckId: deckData.deckId,
      deckName: deckData.deckName,
      description: deckData.description,
      deckType: 'STANDARD',
      isPublic: deckData.isPublic,
      isTournamentLegal: false,
      cards: transformedCards
    };
    const response = await api.post('/decks/save', newFormat);
    return response;
  },

  // 덱 삭제
  deleteDeck: async (deckId: number) => {
    const response = await api.delete(`/decks/${deckId}`);
    return response;
  },

  // 덱에 카드 추가
  addCardToDeck: async (deckId: number, cardData: AddCardToDeckRequest) => {
    const response = await api.post(`/decks/${deckId}/cards`, cardData);
    return response;
  },

  // 덱에서 카드 제거
  removeCardFromDeck: async (deckId: number, cardData: RemoveCardFromDeckRequest) => {
    const response = await api.delete(`/decks/${deckId}/cards`, { data: cardData });
    return response;
  },

  // 랜덤 덱 생성
  generateRandomDeck: async (deckName?: string) => {
    const response = await api.post('/decks/random', { deckName });
    return response;
  },

  // 덱 통계 조회
  getDeckStats: async (deckId: number) => {
    const response = await api.get(`/decks/${deckId}/stats`);
    return response;
  },

  // 실시간 카드 추가 (백엔드 API 호출)
  addCardToDeckAPI: async (deckId: number, cardId: number, quantity: number = 1) => {
    const response = await api.post(`/decks/${deckId}/cards`, {
      cardId,
      quantity
    });
    return response;
  },

  // 실시간 카드 제거 (백엔드 API 호출)
  removeCardFromDeckAPI: async (deckId: number, cardId: number, quantity: number = 1) => {
    const response = await api.delete(`/decks/${deckId}/cards`, {
      data: {
        cardId,
        quantity
      }
    });
    return response;
  },
};

// Export TokenManager for use in components
export { TokenManager };
export default api;