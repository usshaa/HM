import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '@core/services/auth.service';
import { User } from '@shared/models/auth.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  passwordForm!: FormGroup;
  currentUser: User | null = null;
  hideOld = true; hideNew = true; hideConfirm = true;

  constructor(private fb: FormBuilder, private authService: AuthService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.profileForm = this.fb.group({
      name: [this.currentUser?.name || '', Validators.required],
      phone: [this.currentUser?.phone || ''],
      idProofType: [this.currentUser?.idProofType || ''],
      idProofNumber: [this.currentUser?.idProofNumber || ''],
      preferredCurrency: [this.currentUser?.preferredCurrency || 'AED']
    });
    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    });
  }

  saveProfile(): void {
    if (this.profileForm.valid) {
      this.snackBar.open('Profile updated successfully!', 'Close', { duration: 2000 });
    }
  }

  changePassword(): void {
    if (this.passwordForm.valid) {
      if (this.passwordForm.value.newPassword !== this.passwordForm.value.confirmPassword) {
        this.snackBar.open('Passwords do not match!', 'Close', { duration: 3000 });
        return;
      }
      this.snackBar.open('Password changed successfully!', 'Close', { duration: 2000 });
      this.passwordForm.reset();
    }
  }
}
