import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, LoginResponse, User, ApiResponse } from '@shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(
      this.getUserFromStorage()
    );
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  register(user: any): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.apiUrl}/register`, user);
  }

  login(loginRequest: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, loginRequest)
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            this.storeTokens(response.data.accessToken, response.data.refreshToken);
            this.storeUser(response.data.user);
            this.currentUserSubject.next(response.data.user);
          }
        })
      );
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  refreshToken(): Observable<ApiResponse<string>> {
    const refreshToken = this.getRefreshToken();
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/refresh`, {}, {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${refreshToken}`
      })
    }).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.storeTokens(response.data, refreshToken!);
        }
      })
    );
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private storeTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  private storeUser(user: User): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  private getUserFromStorage(): User | null {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  }
}
