import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, User } from '@shared/models/auth.model';
import { AuditLog } from '@shared/models/housekeeping.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/users`);
  }

  toggleUserActive(userId: number, activate: boolean): Observable<ApiResponse<User>> {
    return this.http.patch<ApiResponse<User>>(`${this.apiUrl}/users/${userId}/activate?activate=${activate}`, {});
  }

  createStaff(user: Partial<User>): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.apiUrl}/staff`, user);
  }

  getAuditLogs(from?: number, to?: number): Observable<ApiResponse<any>> {
    let params = new HttpParams();
    if (from) params = params.set('from', from.toString());
    if (to) params = params.set('to', to.toString());
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/audit-logs`, { params });
  }
}
