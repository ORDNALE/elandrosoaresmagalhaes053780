import { Injectable, inject } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable, Subject, timer, EMPTY, ReplaySubject } from 'rxjs';
import { retry, tap, delayWhen, switchAll, catchError } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { TokenService } from '../token.service';

export interface WebSocketMessage {
    type: string;
    payload: unknown;
}

export interface AlbumCreatedEvent {
    albumId: number;
    titulo: string;
    timestamp: string;
    message: string;
}

@Injectable({
    providedIn: 'root'
})
export class WebSocketService {
    private readonly tokenService = inject(TokenService);

    private socket$?: WebSocketSubject<any>;
    private messagesSubject$ = new ReplaySubject<Observable<any>>(1);
    public messages$: Observable<any> = this.messagesSubject$.pipe(switchAll());

    private reconnectInterval = 5000; // 5 seconds
    private reconnectAttempts = 0;
    private maxReconnectAttempts = 10;

    connect(): void {
        if (!this.socket$ || this.socket$.closed) {
            this.socket$ = this.getNewWebSocket();

            const messages = this.socket$.pipe(
                tap({
                    error: (error) => console.error('WebSocket error:', error)
                }),
                retry({
                    count: this.maxReconnectAttempts, // Substitui a lógica manual de max attempts
                    delay: (error, retryCount) => {
                        this.reconnectAttempts = retryCount; // Sincroniza com sua variável se precisar
                        console.log(`WebSocket reconnect attempt ${retryCount}/${this.maxReconnectAttempts}`);

                        // Retorna o timer de delay
                        return timer(this.reconnectInterval);
                    },
                    resetOnSuccess: true // Reseta o contador automaticamente se conectar com sucesso!
                }),
                catchError((error) => {
                    console.error('Max reconnection attempts reached or fatal error:', error);
                    return EMPTY;
                })
            );

            this.messagesSubject$.next(messages);
        }
    }

    private getNewWebSocket(): WebSocketSubject<any> {
        const token = this.tokenService.getAccessToken();
        const wsUrl = token
            ? `${environment.wsUrl}?token=${token}`
            : environment.wsUrl;

        return webSocket<any>({
            url: wsUrl,
            openObserver: {
                next: () => {
                    console.log('WebSocket connected');
                    this.reconnectAttempts = 0;
                }
            },
            closeObserver: {
                next: () => {
                    console.log('WebSocket disconnected');
                }
            }
        });
    }

    sendMessage(message: any): void {
        if (this.socket$) {
            this.socket$.next(message);
        } else {
            console.error('WebSocket is not connected');
        }
    }

    disconnect(): void {
        if (this.socket$) {
            this.socket$.complete();
            this.socket$ = undefined;
        }
    }

    isConnected(): boolean {
        return !!this.socket$ && !this.socket$.closed;
    }

    // Specific method for album creation events
    onAlbumCreated(): Observable<AlbumCreatedEvent> {
        return this.messages$ as Observable<AlbumCreatedEvent>;
    }
}