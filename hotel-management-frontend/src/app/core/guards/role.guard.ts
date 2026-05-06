import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];
    const user = this.authService.getCurrentUser();

    if (!user) {
      this.router.navigate(['/login']);
      return false;
    }

    if (requiredRoles && requiredRoles.length > 0 && !requiredRoles.includes(user.role)) {
      this.router.navigate(['/dashboard']);
      return false;
    }

    return true;
  }
}
