import { Component, OnInit } from '@angular/core';
import { RoomService } from '@core/services/room.service';
import { Room } from '@shared/models/room.model';

@Component({
  selector: 'app-room-list',
  templateUrl: './room-list.component.html',
  styleUrls: ['./room-list.component.scss']
})
export class RoomListComponent implements OnInit {
  rooms: Room[] = [];
  displayedColumns: string[] = ['roomNumber', 'floor', 'roomType', 'status', 'actions'];
  loading = false;

  constructor(private roomService: RoomService) { }

  ngOnInit(): void {
    this.loadRooms();
  }

  loadRooms(): void {
    this.loading = true;
    this.roomService.getRooms().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.rooms = response.data.content || [];
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading rooms', error);
        this.loading = false;
      }
    });
  }

  getStatusColor(status: string): string {
    const statusColors: any = {
      'AVAILABLE': 'green',
      'RESERVED': 'blue',
      'OCCUPIED': 'orange',
      'DIRTY': 'red',
      'CLEANING': 'yellow',
      'CLEAN': 'lightgreen',
      'INSPECTION': 'purple',
      'MAINTENANCE': 'red'
    };
    return statusColors[status] || 'gray';
  }
}
