import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';
import { User, UserRole } from '@shared/models/auth.model';

interface NavItem { label: string; icon: string; route: string; roles: UserRole[]; }

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Hotel Management System';
  isLoggedIn = false;
  currentUser: User | null = null;
  UserRole = UserRole;
  sidenavOpen = true;

  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard', roles: [UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.HOUSEKEEPING, UserRole.GUEST] },
    { label: 'Rooms', icon: 'meeting_room', route: '/rooms', roles: [UserRole.ADMIN, UserRole.RECEPTIONIST] },
    { label: 'Reservations', icon: 'book_online', route: '/reservations', roles: [UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.GUEST] },
    { label: 'Invoices', icon: 'receipt', route: '/invoices', roles: [UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.GUEST] },
    { label: 'Housekeeping', icon: 'cleaning_services', route: '/housekeeping', roles: [UserRole.ADMIN, UserRole.HOUSEKEEPING, UserRole.RECEPTIONIST] },
    { label: 'AI Insights', icon: 'auto_awesome', route: '/insights', roles: [UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.GUEST] },
    { label: 'Admin', icon: 'admin_panel_settings', route: '/admin', roles: [UserRole.ADMIN] },
    { label: 'Profile', icon: 'person', route: '/profile', roles: [UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.HOUSEKEEPING, UserRole.GUEST] },
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.isLoggedIn = !!user;
    });
  }

  isAuthPage(): boolean {
    return this.router.url.includes('/login') || this.router.url.includes('/register');
  }

  getVisibleNavItems(): NavItem[] {
    if (!this.currentUser) return [];
    return this.navItems.filter(item => item.roles.includes(this.currentUser!.role));
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  toggleSidenav(): void {
    this.sidenavOpen = !this.sidenavOpen;
  }
}
