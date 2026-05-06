import { Component, OnInit } from '@angular/core';
import { ReservationService } from '@core/services/reservation.service';
import { Reservation } from '@shared/models/reservation.model';

@Component({
  selector: 'app-reservation-list',
  templateUrl: './reservation-list.component.html',
  styleUrls: ['./reservation-list.component.scss']
})
export class ReservationListComponent implements OnInit {
  reservations: Reservation[] = [];
  displayedColumns: string[] = ['guestName', 'roomType', 'checkInDate', 'checkOutDate', 'status', 'totalAmount', 'actions'];
  loading = false;

  constructor(private reservationService: ReservationService) { }

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.loading = true;
    this.reservationService.getAllReservations().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.reservations = response.data.content || [];
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading reservations', error);
        this.loading = false;
      }
    });
  }

  formatDate(timestamp: number): string {
    return new Date(timestamp).toLocaleDateString();
  }

  getStatusColor(status: string): string {
    const statusColors: any = {
      'PENDING': 'orange',
      'CONFIRMED': 'blue',
      'CHECKED_IN': 'green',
      'CHECKED_OUT': 'gray',
      'CANCELLED': 'red',
      'NO_SHOW': 'red'
    };
    return statusColors[status] || 'gray';
  }
}
