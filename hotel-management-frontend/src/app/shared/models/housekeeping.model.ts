export interface HousekeepingTask {
  id: number;
  roomId: number;
  roomNumber: string;
  assignedToId: number;
  assignedToName: string;
  taskType: TaskType;
  status: TaskStatus;
  priority: Priority;
  scheduledDate: number;
  completedAt?: number;
  notes?: string;
}

export interface MaintenanceRequest {
  id: number;
  roomId: number;
  roomNumber: string;
  reportedById: number;
  reportedByName: string;
  issueDescription: string;
  priority: Priority;
  status: MaintenanceStatus;
  createdAt: number;
  resolvedAt?: number;
}

export enum TaskType {
  CHECKOUT_CLEAN = 'CHECKOUT_CLEAN',
  DAILY_SERVICE = 'DAILY_SERVICE',
  DEEP_CLEAN = 'DEEP_CLEAN',
  INSPECTION = 'INSPECTION'
}

export enum TaskStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  SKIPPED = 'SKIPPED'
}

export enum Priority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}

export enum MaintenanceStatus {
  OPEN = 'OPEN',
  IN_PROGRESS = 'IN_PROGRESS',
  RESOLVED = 'RESOLVED',
  CLOSED = 'CLOSED'
}

export enum ExtraType {
  ROOM_SERVICE = 'ROOM_SERVICE',
  LAUNDRY = 'LAUNDRY',
  MINI_BAR = 'MINI_BAR',
  SPA = 'SPA',
  PARKING = 'PARKING',
  OTHER = 'OTHER'
}

export interface RoomExtra {
  id: number;
  reservationId: number;
  extraType: ExtraType;
  description: string;
  amount: number;
  chargeDate: number;
  addedById?: number;
  addedByName?: string;
}

export interface AuditLog {
  id: number;
  userId: number;
  userName: string;
  action: string;
  entityType: string;
  entityId?: number;
  details?: string;
  timestamp: number;
}
