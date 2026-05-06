import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminService } from '@core/services/admin.service';
import { User, UserRole } from '@shared/models/auth.model';

@Component({
  selector: 'app-user-manager',
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})
export class UserManagerComponent implements OnInit {
  users: User[] = [];
  displayedColumns = ['id', 'name', 'email', 'role', 'phone', 'isActive', 'actions'];
  showForm = false;
  newStaff: any = { role: 'RECEPTIONIST' };
  UserRole = UserRole;
  roles = ['ADMIN', 'RECEPTIONIST', 'HOUSEKEEPING'];

  constructor(private adminService: AdminService, private snackBar: MatSnackBar) {}

  ngOnInit(): void { this.loadUsers(); }

  loadUsers(): void {
    this.adminService.getAllUsers().subscribe(res => {
      if (res.success && res.data) { this.users = res.data.content || res.data || []; }
    });
  }

  toggleActive(user: User): void {
    this.adminService.toggleUserActive(user.id, !user.isActive).subscribe(res => {
      if (res.success) { this.snackBar.open('User status updated!', 'Close', { duration: 2000 }); this.loadUsers(); }
    });
  }

  createStaff(): void {
    if (!this.newStaff.name || !this.newStaff.email) return;
    this.adminService.createStaff(this.newStaff).subscribe({
      next: res => { if (res.success) { this.snackBar.open('Staff account created!', 'Close', { duration: 2000 }); this.showForm = false; this.loadUsers(); } },
      error: err => this.snackBar.open(err.error?.message || 'Error creating staff', 'Close', { duration: 3000 })
    });
  }

  formatDate(ts: number): string { return ts ? new Date(ts).toLocaleDateString() : '-'; }
}
