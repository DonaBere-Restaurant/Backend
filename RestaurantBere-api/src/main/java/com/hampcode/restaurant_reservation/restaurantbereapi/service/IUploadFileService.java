package com.hampcode.restaurant_reservation.restaurantbereapi.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface IUploadFileService {

    public Resource load(String filename) throws MalformedURLException;

    public String copy(MultipartFile multipartFile) throws IOException;

    public boolean delete(String filename);
}
