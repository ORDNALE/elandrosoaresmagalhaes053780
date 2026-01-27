package com.elandroapi.modules.services;

import com.elandroapi.modules.dto.response.CapaAlbumResponse;
import com.elandroapi.modules.entities.Album;
import com.elandroapi.modules.entities.CapaAlbum;
import com.elandroapi.modules.mappers.CapaAlbumMapper;
import com.elandroapi.modules.repositories.AlbumRepository;
import com.elandroapi.modules.repositories.CapaAlbumRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Album album = albumRepository.findByIdOptional(albumId)
                .orElseThrow(() -> new jakarta.ws.rs.NotFoundException("Álbum não encontrado"));

        List<CapaAlbum> capas = new ArrayList<>();

        for (FileUpload file : files) {
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
                capas.add(capa);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao fazer upload da imagem: " + file.fileName(), e);
            }
        }

        return capas.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    private String generateHash(String extension) {
        LocalDate now = LocalDate.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
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