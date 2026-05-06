export interface RoomType {
  id: number;
  typeName: string;
  description: string;
  basePrice: number;
  maxOccupancy: number;
  bedType: BedType;
  amenities: string;
  photoUrl: string;
  isActive: boolean;
}

export enum BedType {
  SINGLE = 'SINGLE',
  DOUBLE = 'DOUBLE',
  KING = 'KING',
  TWIN = 'TWIN'
}

export interface Room {
  id: number;
  roomNumber: string;
  floor: number;
  roomTypeId: number;
  roomType: {
    id: number;
    typeName: string;
    basePrice: number;
  };
  status: RoomStatus;
  isActive: boolean;
  lastCleaned?: number;
  notes?: string;
}

export enum RoomStatus {
  AVAILABLE = 'AVAILABLE',
  RESERVED = 'RESERVED',
  OCCUPIED = 'OCCUPIED',
  DIRTY = 'DIRTY',
  CLEANING = 'CLEANING',
  CLEAN = 'CLEAN',
  INSPECTION = 'INSPECTION',
  MAINTENANCE = 'MAINTENANCE'
}
