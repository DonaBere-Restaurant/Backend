package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.service.IUploadFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
public class IUploadFileServiceImpl implements IUploadFileService {

    private static final DateTimeFormatter FILE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String supabaseServiceKey;

    @Value("${supabase.bucket}")
    private String supabaseBucket;

    @Override
    public Resource load(String filename) throws MalformedURLException {
        String resolved = resolvePublicUrl(filename);
        Resource resource = new UrlResource(URI.create(resolved));
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Error en el recurso: " + resolved);
        }
        return resource;
    }

    @Override
    public String copy(MultipartFile multipartFile, String folder) throws IOException {
        String originalName = multipartFile.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "file";
        }
        String safeName = sanitizeFileName(originalName);
        String timestamp = LocalDateTime.now().format(FILE_TIME_FORMAT);
        String uniqueFileName = timestamp + "_" + UUID.randomUUID() + "_" + safeName;

        String objectPath = buildObjectPath(folder, uniqueFileName);
        String uploadUrl = supabaseUrl.trim() + "/storage/v1/object/" + supabaseBucket + "/" + objectPath;
        String contentType = multipartFile.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uploadUrl))
            .header("Authorization", "Bearer " + supabaseServiceKey)
            .header("apikey", supabaseServiceKey)
            .header("Content-Type", contentType)
            .header("x-upsert", "true")
            .POST(HttpRequest.BodyPublishers.ofByteArray(multipartFile.getBytes()))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new RuntimeException("Error al subir a Supabase: " + status + " - " + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Carga interrumpida", e);
        }

        return buildPublicUrl(objectPath);
    }

    @Override
    public boolean delete(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }
        String objectPath = extractObjectPath(filename);
        String deleteUrl = supabaseUrl.trim() + "/storage/v1/object/" + supabaseBucket + "/" + objectPath;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(deleteUrl))
            .header("Authorization", "Bearer " + supabaseServiceKey)
            .header("apikey", supabaseServiceKey)
            .DELETE()
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            return status >= 200 && status < 300;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String sanitizeFileName(String original) {
        String trimmed = original.trim().replace(" ", "_");
        String ascii = trimmed.replaceAll("[^A-Za-z0-9._-]", "");
        return ascii.isBlank() ? "file" : ascii;
    }

    private String buildObjectPath(String folder, String fileName) {
        String base = (folder == null) ? "" : folder.trim();
        if (base.startsWith("/")) {
            base = base.substring(1);
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (base.isEmpty()) {
            return fileName;
        }
        return base + "/" + fileName;
    }

    private String buildPublicUrl(String objectPath) {
        return supabaseUrl.trim() + "/storage/v1/object/public/" + supabaseBucket + "/" + objectPath;
    }

    private String resolvePublicUrl(String filename) {
        if (filename.startsWith("http://") || filename.startsWith("https://")) {
            return filename;
        }
        return buildPublicUrl(extractObjectPath(filename));
    }

    private String extractObjectPath(String filename) {
        String normalized = filename.trim();
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            String marker = "/storage/v1/object/";
            int index = normalized.indexOf(marker);
            if (index != -1) {
                String tail = normalized.substring(index + marker.length());
                String bucketPrefix = supabaseBucket + "/";
                if (tail.startsWith("public/")) {
                    tail = tail.substring("public/".length());
                }
                if (tail.startsWith(bucketPrefix)) {
                    return tail.substring(bucketPrefix.length());
                }
                return tail;
            }
        }
        String cleaned = normalized.replace("\\", "/");
        return cleaned.startsWith("/") ? cleaned.substring(1) : cleaned;
    }
}
