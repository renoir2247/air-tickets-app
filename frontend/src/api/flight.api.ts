import { apiClient } from './client';
import type { FlightDto } from '../types/dto';

export const getFlights = async (): Promise<FlightDto[]> => {
  const response = await apiClient.get<FlightDto[]>('/flights');
  return response.data;
};

export const searchFlights = async (origin: string, destination: string): Promise<FlightDto[]> => {
  const response = await apiClient.get<FlightDto[]>('/flights/search', {
    params: { origin, destination },
  });
  return response.data;
};
