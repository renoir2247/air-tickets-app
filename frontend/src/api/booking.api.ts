import { apiClient } from './client';
import type { BookingDTO, BookingRequest, BookingStatusDTO } from '../types/dto';

export const createBooking = async (data: BookingRequest): Promise<BookingDTO> => {
  const response = await apiClient.post<BookingDTO>('/bookings', data);
  return response.data;
};

export const cancelBooking = async (id: number): Promise<void> => {
  await apiClient.post(`/bookings/${id}/cancel`);
};

export const getBookings = async (): Promise<BookingDTO[]> => {
  const response = await apiClient.get<BookingDTO[]>('/bookings');
  return response.data;
};

export const getBookingById = async (id: number): Promise<BookingDTO> => {
  const response = await apiClient.get<BookingDTO>(`/bookings/${id}`);
  return response.data;
};

export const getBookingStatus = async (id: number): Promise<BookingStatusDTO> => {
  const response = await apiClient.get<BookingStatusDTO>(`/bookings/${id}/status`);
  return response.data;
};
