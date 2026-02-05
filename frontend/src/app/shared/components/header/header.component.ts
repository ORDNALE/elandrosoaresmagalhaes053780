import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthFacade } from '@core/facades';
import { DialogService } from '@core/services/dialog.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  private readonly authFacade = inject(AuthFacade);
  private readonly dialogService = inject(DialogService);

  readonly isAuthenticated$ = this.authFacade.state$;

  async logout(): Promise<void> {
    const confirmed = await this.dialogService.confirm({
      title: 'Sair',
      message: 'Tem certeza que deseja sair?',
      confirmText: 'Sair',
      type: 'warning'
    });

    if (confirmed) {
      this.authFacade.logout();
    }
  }

  get isAdmin(): boolean {
    return this.authFacade.isAdmin();
  }

  get username(): string | null {
    return this.authFacade.getUsername();
  }
}
