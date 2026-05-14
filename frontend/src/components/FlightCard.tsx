import React from 'react';
import type { FlightDto } from '../types/dto';

interface FlightCardProps {
  flight: FlightDto;
  onSelect?: (flight: FlightDto) => void;
  selected?: boolean;
}

export const FlightCard: React.FC<FlightCardProps> = ({ flight, onSelect, selected }) => {
  const dep = new Date(flight.departureTime).toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
  const arr = new Date(flight.arrivalTime).toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });

  return (
    <div
      className={`rounded-lg border p-4 transition sm:p-5 ${
        selected
          ? 'border-blue-500 bg-blue-50'
          : 'border-gray-200 bg-white hover:shadow-md'
      }`}
    >
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex-1">
          <div className="flex items-center gap-2 text-lg font-bold text-gray-900">
            <span>{flight.flightNumber}</span>
            <span
              className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                flight.status === 'SCHEDULED'
                  ? 'bg-green-100 text-green-700'
                  : flight.status === 'DELAYED'
                  ? 'bg-yellow-100 text-yellow-700'
                  : 'bg-red-100 text-red-700'
              }`}
            >
              {flight.status}
            </span>
          </div>
          <div className="mt-1 text-sm text-gray-600">
            {flight.origin} → {flight.destination}
          </div>
          <div className="mt-1 text-xs text-gray-500">
            {dep} — {arr}
          </div>
        </div>

        <div className="flex items-center justify-between gap-4 sm:flex-col sm:items-end">
          <div className="text-right">
            <div className="text-lg font-bold text-blue-700">{flight.price.toLocaleString('ru-RU')} ₽</div>
            <div className="text-xs text-gray-500">{flight.availableSeats} мест</div>
          </div>
          {onSelect && (
            <button
              onClick={() => onSelect(flight)}
              className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-700 active:bg-blue-800"
            >
              Выбрать
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
