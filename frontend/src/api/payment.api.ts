import { apiClient } from './client';
import type { PaymentDTO, PaymentInitRequest, PaymentWebhookDTO } from '../types/dto';

export const initPayment = async (data: PaymentInitRequest): Promise<PaymentDTO> => {
  const response = await apiClient.post<PaymentDTO>('/payments', data);
  return response.data;
};

export const sendWebhook = async (data: PaymentWebhookDTO): Promise<void> => {
  await apiClient.post('/payments/webhook', data);
};
