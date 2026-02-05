import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthFacade } from '@core/facades';
import { LoginRequest } from '@core/models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authFacade = inject(AuthFacade);

  loginForm: FormGroup;
  isLoading = this.authFacade.isLoading$;
  errorMessage = this.authFacade.error$;

  constructor() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(4)]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {
    const savedEmail = this.authFacade.getRememberedEmail();
    if (savedEmail) {
      this.loginForm.patchValue({
        username: savedEmail,
        rememberMe: true
      });
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { username, password, rememberMe } = this.loginForm.value;

    const loginRequest: LoginRequest = {
      username,
      password
    };

    this.authFacade.login(loginRequest, rememberMe);
  }
}
