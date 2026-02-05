import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardFacade, AuthFacade } from '@core/facades';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
    private readonly dashboardFacade = inject(DashboardFacade);
    readonly authFacade = inject(AuthFacade);

    readonly stats = this.dashboardFacade.stats;
    readonly isLoading = this.dashboardFacade.isLoading;
    readonly error = this.dashboardFacade.error;
    readonly user = this.authFacade.getUsername();

    ngOnInit(): void {
        this.dashboardFacade.loadStats();
    }
}
