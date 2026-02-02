// Authentication Request/Response Models
export interface LoginRequest {
  username: string;
  password: string;
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

// Decoded JWT Token
export interface DecodedToken {
  sub: string; // username
  exp: number; // expiration timestamp
  iat: number; // issued at timestamp
  groups: string[]; // roles: ['USER', 'ADMIN']
}

// Authentication State
export interface AuthState {
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshToken: string | null;
  username: string | null;
  roles: string[];
}
