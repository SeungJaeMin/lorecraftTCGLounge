import api from './api';

export interface LoginRequest {
  userid: string;
  password: string;
}

export interface SignupRequest {
  userid: string;
  password: string;
  email: string;
  phoneNumber?: string;
  userType: 'GAMER' | 'STORE_OWNER' | 'ADMIN';
  nickname?: string;
  storeName?: string;
  storeLocation?: string;
  storeZipcode?: string;
  businessLicense?: string;
  contactNumber?: string;
  employeeId?: string;
  department?: string;
}

export interface User {
  id: number;
  userid: string;
  email: string;
  userType: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    tokenType: string;
    id: number;
    userid: string;
    email: string;
    userType: string;
  };
}

class AuthService {
  private tokenKey = 'tcg_lounge_token';
  private userKey = 'tcg_lounge_user';

  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<AuthResponse>('/auth/login', credentials);
      
      if (response.data.success) {
        this.setToken(response.data.data.token);
        this.setUser({
          id: response.data.data.id,
          userid: response.data.data.userid,
          email: response.data.data.email,
          userType: response.data.data.userType,
        });
      }
      
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '로그인에 실패했습니다.');
    }
  }

  async signup(userData: SignupRequest): Promise<any> {
    try {
      const response = await api.post('/auth/signup', userData);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || '회원가입에 실패했습니다.');
    }
  }

  async checkUseridAvailability(userid: string): Promise<boolean> {
    try {
      const response = await api.get(`/auth/check/userid/${userid}`);
      return response.data.data;
    } catch (error) {
      return false;
    }
  }

  async checkEmailAvailability(email: string): Promise<boolean> {
    try {
      const response = await api.get(`/auth/check/email/${email}`);
      return response.data.data;
    } catch (error) {
      return false;
    }
  }

  async getCurrentUser(): Promise<User | null> {
    try {
      const token = this.getToken();
      if (!token) return null;

      const response = await api.get('/auth/me');
      return response.data.data;
    } catch (error) {
      this.logout();
      return null;
    }
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    window.location.href = '/';
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getUser(): User | null {
    const userStr = localStorage.getItem(this.userKey);
    return userStr ? JSON.parse(userStr) : null;
  }

  setUser(user: User): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    const user = this.getUser();
    return !!(token && user);
  }

  hasRole(role: string): boolean {
    const user = this.getUser();
    return user?.userType === role;
  }
}

export default new AuthService();