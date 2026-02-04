import { TestBed } from '@angular/core/testing';
import { ArtistFacade } from './artist.facade';
import { ArtistApiService } from '../services/api';
import { ArtistStateService } from '../state';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ArtistaRequest, ArtistaResponse, PageRequest, TipoArtista } from '@core/models';

describe('ArtistFacade', () => {
    let facade: ArtistFacade;
    let artistApiSpy: jasmine.SpyObj<ArtistApiService>;
    let artistStateSpy: jasmine.SpyObj<ArtistStateService>;
    let notificationSpy: jasmine.SpyObj<NotificationService>;
    let routerSpy: jasmine.SpyObj<Router>;

    const mockArtist: ArtistaResponse = {
        id: 1,
        nome: 'Test Artist',
        tipo: TipoArtista.SOLO,
        quantidadeAlbuns: 0,
        albuns: []
    };

    beforeEach(() => {
        artistApiSpy = jasmine.createSpyObj('ArtistApiService', ['list', 'getById', 'create', 'update', 'delete']);
        artistStateSpy = jasmine.createSpyObj('ArtistStateService', [
            'setLoading', 'clearError', 'setArtists', 'setPagination', 'setError',
            'setSelectedArtist', 'addArtist', 'updateArtist', 'removeArtist', 'reset'
        ]);
        notificationSpy = jasmine.createSpyObj('NotificationService', ['success', 'error']);
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        (artistStateSpy as any).artistList$ = new BehaviorSubject([]);
        (artistStateSpy as any).selectedArtist = new BehaviorSubject(null);
        (artistStateSpy as any).isLoading$ = new BehaviorSubject(false);
        (artistStateSpy as any).error$ = new BehaviorSubject(null);
        (artistStateSpy as any).page$ = new BehaviorSubject(0);
        (artistStateSpy as any).pageCount$ = new BehaviorSubject(0);
        (artistStateSpy as any).total$ = new BehaviorSubject(0);

        TestBed.configureTestingModule({
            providers: [
                ArtistFacade,
                { provide: ArtistApiService, useValue: artistApiSpy },
                { provide: ArtistStateService, useValue: artistStateSpy },
                { provide: NotificationService, useValue: notificationSpy },
                { provide: Router, useValue: routerSpy }
            ]
        });

        facade = TestBed.inject(ArtistFacade);
    });

    it('deve ser criado com sucesso', () => {
        expect(facade).toBeTruthy();
    });

    describe('obterArtistas', () => {
        it('deve carregar artistas e atualizar estado', () => {
            const pageRequest: PageRequest = { page: 0, size: 10 };
            const response = {
                content: [mockArtist],
                page: 0,
                pageCount: 1,
                total: 1,
                size: 10
            };

            artistApiSpy.list.and.returnValue(of(response));

            facade.loadArtists(pageRequest);

            expect(artistStateSpy.setLoading).toHaveBeenCalledWith(true);
            expect(artistStateSpy.setArtists).toHaveBeenCalledWith(response.content);
            expect(artistStateSpy.setPagination).toHaveBeenCalledWith(0, 1, 1);
            expect(artistStateSpy.setLoading).toHaveBeenCalledWith(false);
        });
    });

    describe('criarArtista', () => {
        it('deve criar artista e notificar successo', () => {
            const request: ArtistaRequest = { nome: 'New Artist', tipo: TipoArtista.BANDA };
            artistApiSpy.create.and.returnValue(of(mockArtist));

            facade.createArtist(request);

            expect(artistApiSpy.create).toHaveBeenCalledWith(request);
            expect(artistStateSpy.addArtist).toHaveBeenCalledWith(mockArtist);
            expect(notificationSpy.success).toHaveBeenCalled();
            expect(routerSpy.navigate).toHaveBeenCalledWith(['/artists']);
        });
    });

    describe('deletarArtista', () => {
        it('deve deletar artista e navegar se redirectTo informado', () => {
            artistApiSpy.delete.and.returnValue(of(void 0));

            facade.deleteArtist(1, '/artists');

            expect(artistApiSpy.delete).toHaveBeenCalledWith(1);
            expect(artistStateSpy.removeArtist).toHaveBeenCalledWith(1);
            expect(notificationSpy.success).toHaveBeenCalled();
            expect(routerSpy.navigate).toHaveBeenCalledWith(['/artists']);
        });

        it('deve deletar artista e NÃO navegar se redirectTo ausente', () => {
            artistApiSpy.delete.and.returnValue(of(void 0));

            facade.deleteArtist(1);

            expect(artistApiSpy.delete).toHaveBeenCalledWith(1);
            expect(routerSpy.navigate).not.toHaveBeenCalled();
        });
    });
});
