import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '@shared/models/auth.model';
import { HousekeepingTask, MaintenanceRequest } from '@shared/models/housekeeping.model';

@Injectable({ providedIn: 'root' })
export class HousekeepingService {
  private apiUrl = 'http://localhost:8080/api/housekeeping';

  constructor(private http: HttpClient) {}

  getTasks(assignedTo?: number): Observable<ApiResponse<any>> {
    let params = new HttpParams();
    if (assignedTo) params = params.set('assignedTo', assignedTo.toString());
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/tasks`, { params });
  }

  createTask(task: Partial<HousekeepingTask>): Observable<ApiResponse<HousekeepingTask>> {
    return this.http.post<ApiResponse<HousekeepingTask>>(`${this.apiUrl}/tasks`, task);
  }

  updateTaskStatus(taskId: number, status: string): Observable<ApiResponse<HousekeepingTask>> {
    return this.http.patch<ApiResponse<HousekeepingTask>>(`${this.apiUrl}/tasks/${taskId}/status?status=${status}`, {});
  }

  getMaintenanceRequests(status?: string): Observable<ApiResponse<any>> {
    let params = new HttpParams();
    if (status) params = params.set('status', status);
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/maintenance`, { params });
  }

  createMaintenanceRequest(req: Partial<MaintenanceRequest>): Observable<ApiResponse<MaintenanceRequest>> {
    return this.http.post<ApiResponse<MaintenanceRequest>>(`${this.apiUrl}/maintenance`, req);
  }

  updateMaintenanceStatus(id: number, status: string): Observable<ApiResponse<MaintenanceRequest>> {
    return this.http.patch<ApiResponse<MaintenanceRequest>>(`${this.apiUrl}/maintenance/${id}/status?status=${status}`, {});
  }
}
