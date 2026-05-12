import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { BookingDTO, FlightDto } from '../types/dto';

interface BookingStore {
  selectedFlight: FlightDto | null;
  currentBooking: BookingDTO | null;
  setSelectedFlight: (flight: FlightDto | null) => void;
  setCurrentBooking: (booking: BookingDTO | null) => void;
  clearBooking: () => void;
}

export const useBookingStore = create<BookingStore>()(
  persist(
    (set) => ({
      selectedFlight: null,
      currentBooking: null,
      setSelectedFlight: (flight) => set({ selectedFlight: flight }),
      setCurrentBooking: (booking) => set({ currentBooking: booking }),
      clearBooking: () => set({ selectedFlight: null, currentBooking: null }),
    }),
    {
      name: 'aviacassa-booking',
      storage: {
        getItem: (name) => {
          const str = sessionStorage.getItem(name);
          return str ? JSON.parse(str) : null;
        },
        setItem: (name, value) => sessionStorage.setItem(name, JSON.stringify(value)),
        removeItem: (name) => sessionStorage.removeItem(name),
      },
    }
  )
);
