export interface Reservation {
  id: number;
  guestId: number;
  guestName: string;
  roomId?: number;
  roomNumber?: string;
  roomTypeId: number;
  roomTypeName: string;
  checkInDate: number;
  checkOutDate: number;
  numAdults: number;
  numChildren?: number;
  status: ReservationStatus;
  source: BookingSource;
  specialRequests?: string;
  totalAmount: number;
  earlyCheckInRequested?: boolean;
  lateCheckOutRequested?: boolean;
  createdAt: number;
}

export enum ReservationStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CHECKED_IN = 'CHECKED_IN',
  CHECKED_OUT = 'CHECKED_OUT',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW'
}

export enum BookingSource {
  DIRECT = 'DIRECT',
  PORTAL = 'PORTAL',
  WALK_IN = 'WALK_IN'
}
