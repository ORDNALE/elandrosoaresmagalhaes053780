package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.CapaAlbum;
import com.elandroapi.modules.mappers.CapaAlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.CapaAlbumRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CapaAlbumService {

    @Inject
    MinioClient minioClient;

    @Inject
    CapaAlbumRepository repository;

    @Inject
    AlbumRepository albumRepository;

    @Inject
    CapaAlbumMapper mapper;

    @ConfigProperty(name = "minio.bucket-name")
    String bucketName;

    @Transactional
    public List<CapaAlbumResponse> uploadCapas(Long albumId, List<FileUpload> files) {
        Album album = buscarAlbum(albumId);

        return files.stream()
                .map(file -> uploadCapa(album, file))
                .map(mapper::toResponse)
                .toList();
    }

    public List<CapaAlbumResponse> listarCapas(Long albumId) {
        buscarAlbum(albumId);
        return repository.find("album.id", albumId).list().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CapaAlbumResponse buscarCapa(Long albumId, Long capaId) {
        buscarAlbum(albumId);
        CapaAlbum capa = repository.find("id = ?1 and album.id = ?2", capaId, albumId)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException(
                        String.format("Capa %s não encontrada no álbum %s", capaId, albumId)));
        return mapper.toResponse(capa);
    }

    @Transactional
    public void excluirCapa(Long albumId, Long capaId) {
        buscarAlbum(albumId);
        CapaAlbum capa = repository.find("id = ?1 and album.id = ?2", capaId, albumId)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException(
                        String.format("Capa %s não encontrada no álbum %s", capaId, albumId)));

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(capa.getBucket())
                            .object(capa.getHash())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover imagem do MinIO: " + capa.getHash(), e);
        }

        repository.delete(capa);
    }

    private CapaAlbum uploadCapa(Album album, FileUpload file) {
        String extension = getExtension(file.fileName());
        String hash = generateHash(extension);

        try (InputStream is = Files.newInputStream(file.filePath())) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(hash)
                            .stream(is, file.size(), -1)
                            .contentType(file.contentType())
                            .build());

            CapaAlbum capa = new CapaAlbum();
            capa.setAlbum(album);
            capa.setBucket(bucketName);
            capa.setHash(hash);
            capa.setContentType(file.contentType());
            capa.setTamanho(file.size());

            repository.persist(capa);
            return capa;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da imagem: " + file.fileName(), e);
        }
    }

    private Album buscarAlbum(Long albumId) {
        return albumRepository.findByIdOptional(albumId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Álbum %s não encontrado", albumId)));
    }

    private String generateHash(String extension) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%s%s", datePath, uuid, extension);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
