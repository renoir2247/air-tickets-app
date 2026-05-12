import { apiClient } from './client';
import type { ReportDTO } from '../types/dto';

export const getSalesReport = async (from: string, to: string): Promise<ReportDTO> => {
  const response = await apiClient.get<ReportDTO>('/reports/sales', {
    params: { from, to },
  });
  return response.data;
};
