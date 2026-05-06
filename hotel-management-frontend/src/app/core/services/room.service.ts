import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Room, RoomType, RoomStatus } from '@shared/models/room.model';
import { ApiResponse } from '@shared/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private apiUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient) { }

  getRoomTypes(page: number = 0, size: number = 20): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/types`, { params });
  }

  createRoomType(roomType: RoomType): Observable<ApiResponse<RoomType>> {
    return this.http.post<ApiResponse<RoomType>>(`${this.apiUrl}/types`, roomType);
  }

  getRooms(page: number = 0, size: number = 20): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<any>>(this.apiUrl, { params });
  }

  createRoom(room: Room): Observable<ApiResponse<Room>> {
    return this.http.post<ApiResponse<Room>>(this.apiUrl, room);
  }

  updateRoomStatus(roomId: number, status: RoomStatus): Observable<ApiResponse<Room>> {
    return this.http.patch<ApiResponse<Room>>(
      `${this.apiUrl}/${roomId}/status`,
      {},
      { params: new HttpParams().set('status', status) }
    );
  }

  searchAvailableRooms(roomTypeId: number, checkInDate: number, checkOutDate: number): Observable<ApiResponse<any>> {
    const params = new HttpParams()
      .set('roomTypeId', roomTypeId.toString())
      .set('checkInDate', checkInDate.toString())
      .set('checkOutDate', checkOutDate.toString());
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/available`, { params });
  }
}
