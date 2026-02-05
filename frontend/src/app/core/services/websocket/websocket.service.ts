import { Injectable, inject } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable, timer, EMPTY, ReplaySubject } from 'rxjs';
import { retry, tap, switchAll, catchError } from 'rxjs/operators';
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
    private maxReconnectAttempts = 10;

    connect(): void {
        if (!this.socket$ || this.socket$.closed) {
            this.socket$ = this.getNewWebSocket();

            const messages = this.socket$.pipe(
                tap({
                    error: (error) => console.error('WebSocket error:', error)
                }),
                retry({
                    count: this.maxReconnectAttempts,
                    delay: (error, retryCount) => {
                        console.log(`Tentativa de reconexão do WebSocket ${retryCount}/${this.maxReconnectAttempts}`);
                        return timer(this.reconnectInterval);
                    },
                    resetOnSuccess: true
                }),
                catchError((error) => {
                    console.error('Número máximo de tentativas de reconexão atingido ou erro fatal:', error);
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
                    console.log('WebSocket conectado');
                }
            },
            closeObserver: {
                next: () => {
                    console.log('WebSocket desconectado');
                }
            }
        });
    }

    sendMessage(message: any): void {
        if (this.socket$) {
            this.socket$.next(message);
        } else {
            console.error('WebSocket não está conectado');
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

    onAlbumCreated(): Observable<AlbumCreatedEvent> {
        return this.messages$ as Observable<AlbumCreatedEvent>;
    }
}
