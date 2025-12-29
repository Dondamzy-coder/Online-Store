package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateImageRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.ImageResponse;
import com.codewithdondamzy.onlinestore.Models.Image;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {
    ImageResponse createImage(CreateImageRequest createImageRequest);
    ImageResponse getImageByFileName(String fileName);
    ImageResponse getImageByUrl(String url);
    ImageResponse getImageByName(String name);
    ImageResponse deleteImage(Long id);
    ImageResponse deleteImageByUrl(String url);
    ImageResponse updateImage(MultipartFile file, Long id);
}
