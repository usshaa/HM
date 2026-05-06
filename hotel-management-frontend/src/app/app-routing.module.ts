import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@core/guards/auth.guard';
import { UserRole } from '@shared/models/auth.model';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '', loadChildren: () => import('@features/auth/auth.module').then(m => m.AuthModule) },
  { path: 'dashboard', loadChildren: () => import('@features/dashboard/dashboard.module').then(m => m.DashboardModule), canActivate: [AuthGuard] },
  { path: 'rooms', loadChildren: () => import('@features/rooms/rooms.module').then(m => m.RoomsModule), canActivate: [AuthGuard], data: { roles: [UserRole.ADMIN, UserRole.RECEPTIONIST] } },
  { path: 'reservations', loadChildren: () => import('@features/reservations/reservations.module').then(m => m.ReservationsModule), canActivate: [AuthGuard], data: { roles: [UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.GUEST] } },
  { path: 'invoices', loadChildren: () => import('@features/invoices/invoices.module').then(m => m.InvoicesModule), canActivate: [AuthGuard], data: { roles: [UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.GUEST] } },
  { path: 'housekeeping', loadChildren: () => import('@features/housekeeping/housekeeping.module').then(m => m.HousekeepingModule), canActivate: [AuthGuard], data: { roles: [UserRole.ADMIN, UserRole.HOUSEKEEPING, UserRole.RECEPTIONIST] } },
  { path: 'admin', loadChildren: () => import('@features/admin/admin.module').then(m => m.AdminModule), canActivate: [AuthGuard], data: { roles: [UserRole.ADMIN] } },
  { path: 'insights', loadChildren: () => import('@features/insights/insights.module').then(m => m.InsightsModule), canActivate: [AuthGuard] },
  { path: 'profile', loadChildren: () => import('@features/profile/profile.module').then(m => m.ProfileModule), canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false, useHash: false })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
