import { TestBed } from '@angular/core/testing';
import { AlbumFacade } from './album.facade';
import { AlbumApiService, CoverApiService } from '../services/api';
import { AlbumStateService } from '../state';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';
import { WebSocketService } from '../services/websocket/websocket.service';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AlbumRequest, AlbumResponse, PageRequest } from '@core/models';

describe('AlbumFacade', () => {
    let facade: AlbumFacade;
    let albumApiSpy: jasmine.SpyObj<AlbumApiService>;
    let albumStateSpy: jasmine.SpyObj<AlbumStateService>;
    let notificationSpy: jasmine.SpyObj<NotificationService>;
    let routerSpy: jasmine.SpyObj<Router>;
    let coverApiSpy: jasmine.SpyObj<CoverApiService>;
    let wsServiceSpy: jasmine.SpyObj<WebSocketService>;

    const mockAlbum: AlbumResponse = {
        id: 1,
        titulo: 'Test Album',
        artistas: [],
        capas: []
    };

    beforeEach(() => {
        albumApiSpy = jasmine.createSpyObj('AlbumApiService', ['list', 'getById', 'create', 'update', 'delete']);
        albumStateSpy = jasmine.createSpyObj('AlbumStateService', [
            'setLoading', 'clearError', 'setAlbums', 'setPagination', 'setError',
            'setSelectedAlbum', 'addAlbum', 'updateAlbum', 'removeAlbum', 'reset'
        ]);
        notificationSpy = jasmine.createSpyObj('NotificationService', ['success', 'error']);
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);
        coverApiSpy = jasmine.createSpyObj('CoverApiService', ['upload']);
        wsServiceSpy = jasmine.createSpyObj('WebSocketService', ['isConnected']);

        wsServiceSpy.isConnected.and.returnValue(true);

        (albumStateSpy as any).albumList$ = new BehaviorSubject([]);
        (albumStateSpy as any).selectedAlbum = new BehaviorSubject(null);
        (albumStateSpy as any).isLoading$ = new BehaviorSubject(false);
        (albumStateSpy as any).error$ = new BehaviorSubject(null);
        (albumStateSpy as any).page$ = new BehaviorSubject(0);
        (albumStateSpy as any).pageCount$ = new BehaviorSubject(0);
        (albumStateSpy as any).total$ = new BehaviorSubject(0);

        TestBed.configureTestingModule({
            providers: [
                AlbumFacade,
                { provide: AlbumApiService, useValue: albumApiSpy },
                { provide: AlbumStateService, useValue: albumStateSpy },
                { provide: NotificationService, useValue: notificationSpy },
                { provide: Router, useValue: routerSpy },
                { provide: CoverApiService, useValue: coverApiSpy },
                { provide: WebSocketService, useValue: wsServiceSpy }
            ]
        });

        facade = TestBed.inject(AlbumFacade);
    });

    it('should be created', () => {
        expect(facade).toBeTruthy();
    });

    describe('loadAlbums', () => {
        it('should load albums and update state on success', () => {
            const pageRequest: PageRequest = { page: 0, size: 10 };
            const response = {
                content: [mockAlbum],
                page: 0,
                pageCount: 1,
                total: 1,
                size: 10
            };

            albumApiSpy.list.and.returnValue(of(response));

            facade.loadAlbums(pageRequest);

            expect(albumStateSpy.setLoading).toHaveBeenCalledWith(true);
            expect(albumStateSpy.clearError).toHaveBeenCalled();
            expect(albumApiSpy.list).toHaveBeenCalledWith(pageRequest, undefined);
            expect(albumStateSpy.setAlbums).toHaveBeenCalledWith(response.content);
            expect(albumStateSpy.setPagination).toHaveBeenCalledWith(0, 1, 1);
            expect(albumStateSpy.setLoading).toHaveBeenCalledWith(false);
        });

        it('should handle error when loading albums', () => {
            const pageRequest: PageRequest = { page: 0, size: 10 };
            const error = new HttpErrorResponse({ error: { message: 'Fetch error' } });

            albumApiSpy.list.and.returnValue(throwError(() => error));

            facade.loadAlbums(pageRequest);

            expect(albumStateSpy.setError).toHaveBeenCalledWith('Fetch error');
            expect(albumStateSpy.setLoading).toHaveBeenCalledWith(false);
            expect(notificationSpy.error).toHaveBeenCalledWith('Fetch error');
        });
    });

    describe('createAlbum', () => {
        const request: AlbumRequest = { titulo: 'New Album', artistaIds: [1] };

        it('should create album and handle file upload', () => {
            const files = [new File([''], 'cover.jpg')];

            albumApiSpy.create.and.returnValue(of(mockAlbum));
            coverApiSpy.upload.and.returnValue(of([]));

            facade.createAlbum(request, files);

            expect(albumStateSpy.setLoading).toHaveBeenCalledWith(true);
            expect(albumApiSpy.create).toHaveBeenCalledWith(request);
            expect(coverApiSpy.upload).toHaveBeenCalledWith(mockAlbum.id, files);
            expect(albumStateSpy.addAlbum).toHaveBeenCalledWith(mockAlbum);
            expect(routerSpy.navigate).toHaveBeenCalledWith(['/albums']);
        });

        it('should notify success via fallback if WS is not connected', () => {
            wsServiceSpy.isConnected.and.returnValue(false);
            albumApiSpy.create.and.returnValue(of(mockAlbum));

            facade.createAlbum(request);

            expect(notificationSpy.success).toHaveBeenCalledWith(jasmine.stringMatching(/offline mode/i));
        });

        it('should NOT notify success if WS IS connected (waits for event)', () => {
            wsServiceSpy.isConnected.and.returnValue(true);
            albumApiSpy.create.and.returnValue(of(mockAlbum));

            facade.createAlbum(request);

            expect(notificationSpy.success).not.toHaveBeenCalled();
        });
    });

    describe('updateAlbum', () => {
        it('should update album, upload files and reload', () => {
            const request: AlbumRequest = { titulo: 'Updated Album', artistaIds: [1] };
            const files = [new File([''], 'cover.jpg')];
            const updatedAlbum = { ...mockAlbum, titulo: 'Updated Album' };

            albumApiSpy.update.and.returnValue(of(void 0));
            coverApiSpy.upload.and.returnValue(of([]));
            albumApiSpy.getById.and.returnValue(of(updatedAlbum));

            facade.updateAlbum(1, request, files);

            expect(albumApiSpy.update).toHaveBeenCalledWith(1, request);
            expect(coverApiSpy.upload).toHaveBeenCalledWith(1, files);
            expect(albumApiSpy.getById).toHaveBeenCalledWith(1);
            expect(albumStateSpy.updateAlbum).toHaveBeenCalledWith(1, updatedAlbum);
            expect(notificationSpy.success).toHaveBeenCalled();
            expect(routerSpy.navigate).toHaveBeenCalledWith(['/albums']);
        });
    });

    describe('deleteAlbum', () => {
        it('should delete album and remove from state', () => {
            albumApiSpy.delete.and.returnValue(of(void 0));

            facade.deleteAlbum(1);

            expect(albumApiSpy.delete).toHaveBeenCalledWith(1);
            expect(albumStateSpy.removeAlbum).toHaveBeenCalledWith(1);
            expect(notificationSpy.success).toHaveBeenCalled();
        });
    });
});
