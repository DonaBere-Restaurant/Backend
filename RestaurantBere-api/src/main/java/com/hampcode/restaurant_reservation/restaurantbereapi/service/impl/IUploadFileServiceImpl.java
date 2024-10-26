package com.hampcode.restaurant_reservation.restaurantbereapi.service.impl;

import com.hampcode.restaurant_reservation.restaurantbereapi.service.IUploadFileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class IUploadFileServiceImpl implements IUploadFileService {

    //private final static String UPLOADS_FOLDER = "uploads/";
    private final static String UPLOADS_FOLDER = "D:/SEXTOCICLO/proyecto_inso/Backend/RestaurantBere-api/uploads/";

    @Override
    public Resource load(String filename) throws MalformedURLException {
        Path path = getPath(filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Error en el path" + path.toString());
        }
        return resource;
    }

    @Override
    public String copy(MultipartFile multipartFile) throws IOException {

        Path uploadDir = Paths.get(UPLOADS_FOLDER);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String uniqueFileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
        //Path rootPath = getPath(uniqueFileName);
        Path rootPath = uploadDir.resolve(uniqueFileName).toAbsolutePath();

        //Files.copy(multipartFile.getInputStream(), rootPath);
        Files.copy(multipartFile.getInputStream(), rootPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    @Override
    public boolean delete(String filename) {
        Path rootPath = getPath(filename);
        File file = rootPath.toFile();
        if (file.exists() && file.canRead()) {
            if (file.delete()) {
                return true;
            }
        }
        return false;
    }

    public Path getPath(String filename) {
        return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
    }
}
