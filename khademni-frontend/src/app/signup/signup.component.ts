import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  user = { username: '', email: '', password: '', role: 'worker' };
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  signup() {
    this.authService.signup(this.user).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        this.router.navigate([response.role === 'worker' ? '/worker-dashboard' : '/employer-dashboard']);
      },
      error: (err) => {
        this.errorMessage = err.error || 'Signup failed';
      }
    });
  }
}