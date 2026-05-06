import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HousekeepingService } from '@core/services/housekeeping.service';
import { MaintenanceRequest, MaintenanceStatus, Priority } from '@shared/models/housekeeping.model';

@Component({
  selector: 'app-maintenance-list',
  templateUrl: './maintenance-list.component.html',
  styleUrls: ['./maintenance-list.component.scss']
})
export class MaintenanceListComponent implements OnInit {
  requests: MaintenanceRequest[] = [];
  displayedColumns = ['id', 'roomNumber', 'issueDescription', 'priority', 'status', 'reportedByName', 'createdAt', 'actions'];
  showForm = false;
  newRequest: Partial<MaintenanceRequest> = { priority: Priority.MEDIUM };

  constructor(private housekeepingService: HousekeepingService, private snackBar: MatSnackBar) {}

  ngOnInit(): void { this.loadRequests(); }

  loadRequests(): void {
    this.housekeepingService.getMaintenanceRequests().subscribe(res => {
      if (res.success && res.data) { this.requests = res.data.content || res.data || []; }
    });
  }

  createRequest(): void {
    if (!this.newRequest.roomId || !this.newRequest.issueDescription || !this.newRequest.reportedById) return;
    this.housekeepingService.createMaintenanceRequest(this.newRequest).subscribe(res => {
      if (res.success) { this.snackBar.open('Request created!', 'Close', { duration: 2000 }); this.showForm = false; this.loadRequests(); }
    });
  }

  updateStatus(id: number, status: string): void {
    this.housekeepingService.updateMaintenanceStatus(id, status).subscribe(res => {
      if (res.success) { this.snackBar.open('Status updated!', 'Close', { duration: 2000 }); this.loadRequests(); }
    });
  }

  getPriorityColor(priority: string): string {
    const colors: { [key: string]: string } = { LOW: '#4caf50', MEDIUM: '#ff9800', HIGH: '#f44336', URGENT: '#9c27b0' };
    return colors[priority] || '#757575';
  }

  formatDate(ts: number): string { return ts ? new Date(ts).toLocaleDateString() : '-'; }
}
