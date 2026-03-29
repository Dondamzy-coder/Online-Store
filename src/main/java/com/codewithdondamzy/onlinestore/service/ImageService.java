package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Response.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface ImageService {
    ImageResponse createImage(MultipartFile file) throws IOException;

    ImageResponse getImageById(Long id);

    ImageResponse saveImages(List<MultipartFile> files, Long productId);

    ImageResponse getImageByFileName(String fileName);

    ImageResponse getImageByUrl(String url);

    ImageResponse deleteImage(Long id);

    ImageResponse deleteImageByUrl(String url);

    ImageResponse updateImage(MultipartFile file, Long id);
}
