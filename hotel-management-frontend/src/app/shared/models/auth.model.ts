export interface User {
  id: number;
  email: string;
  name: string;
  phone: string;
  role: UserRole;
  idProofType?: string;
  idProofNumber?: string;
  preferredCurrency?: string;
  isActive: boolean;
  createdAt: number;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  RECEPTIONIST = 'RECEPTIONIST',
  HOUSEKEEPING = 'HOUSEKEEPING',
  GUEST = 'GUEST'
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
  tokenType: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  errors?: string[];
}
