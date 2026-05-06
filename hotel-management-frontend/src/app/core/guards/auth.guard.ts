import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authService.isLoggedIn()) {
      const user = this.authService.getCurrentUser();
      
      // Check if route requires specific role
      if (route.data['roles'] && route.data['roles'].length > 0) {
        if (route.data['roles'].includes(user?.role)) {
          return true;
        }
        this.router.navigate(['/unauthorized']);
        return false;
      }

      return true;
    }

    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
