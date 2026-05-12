import { apiClient } from './client';
import type { AuthResponse, LoginRequest } from '../types/dto';

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  const response = await apiClient.post<AuthResponse>('/auth/login', data);
  return response.data;
};
