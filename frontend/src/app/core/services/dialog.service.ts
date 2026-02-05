import { Injectable, signal } from '@angular/core';

export interface ConfirmationDialogData {
    title: string;
    message: string;
    confirmText?: string;
    cancelText?: string;
    type?: 'danger' | 'warning' | 'info';
}

@Injectable({
    providedIn: 'root'
})
export class DialogService {
    readonly isOpen = signal(false);
    readonly data = signal<ConfirmationDialogData | null>(null);

    private resolvePromise?: (value: boolean) => void;

    confirm(data: ConfirmationDialogData): Promise<boolean> {
        this.data.set(data);
        this.isOpen.set(true);

        return new Promise<boolean>((resolve) => {
            this.resolvePromise = resolve;
        });
    }

    close(result: boolean): void {
        this.isOpen.set(false);
        if (this.resolvePromise) {
            this.resolvePromise(result);
            this.resolvePromise = undefined;
        }
    }
}
