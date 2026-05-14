import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getFlights } from '../api/flight.api';
import { FlightCard } from '../components/FlightCard';
import { Loader } from '../components/Loader';
import { useBookingStore } from '../store/bookingStore';
import type { FlightDto } from '../types/dto';

export const SearchPage: React.FC = () => {
  const navigate = useNavigate();
  const setSelectedFlight = useBookingStore((s) => s.setSelectedFlight);
  const [flights, setFlights] = useState<FlightDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [originFilter, setOriginFilter] = useState('');
  const [destFilter, setDestFilter] = useState('');

  useEffect(() => {
    const fetchFlights = async () => {
      try {
        const data = await getFlights();
        setFlights(data);
      } catch {
        setError('Не удалось загрузить рейсы');
      } finally {
        setLoading(false);
      }
    };
    fetchFlights();
  }, []);

  const filteredFlights = flights.filter((f) => {
    const matchOrigin = originFilter
      ? f.origin.toLowerCase().includes(originFilter.toLowerCase())
      : true;
    const matchDest = destFilter
      ? f.destination.toLowerCase().includes(destFilter.toLowerCase())
      : true;
    return matchOrigin && matchDest;
  });

  const handleSelect = (flight: FlightDto) => {
    setSelectedFlight(flight);
    navigate('/booking');
  };

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold text-gray-900 sm:text-2xl">Поиск рейсов</h2>

      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Откуда</label>
          <input
            type="text"
            value={originFilter}
            onChange={(e) => setOriginFilter(e.target.value)}
            placeholder="Москва"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>
        <div>
          <label className="mb-1 block text-sm font-medium text-gray-700">Куда</label>
          <input
            type="text"
            value={destFilter}
            onChange={(e) => setDestFilter(e.target.value)}
            placeholder="Санкт-Петербург"
            className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>
      </div>

      {loading && <Loader className="py-12" />}

      {error && (
        <div className="rounded-md bg-red-50 p-4 text-sm text-red-700">{error}</div>
      )}

      {!loading && !error && (
        <div className="space-y-3">
          {filteredFlights.length === 0 ? (
            <p className="py-8 text-center text-gray-500">Рейсы не найдены</p>
          ) : (
            filteredFlights.map((flight) => (
              <FlightCard key={flight.id} flight={flight} onSelect={handleSelect} />
            ))
          )}
        </div>
      )}
    </div>
  );
};
