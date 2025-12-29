package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateImageRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.ImageResponse;
import com.codewithdondamzy.onlinestore.Models.Image;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ProductService  productService;

    public ImageServiceImpl(ImageRepository imageRepository, ProductService productService) {
        this.imageRepository = imageRepository;
        this.productService = productService;
    }
    @Override
    public ImageResponse createImage(CreateImageRequest createImageRequest) {
        ImageResponse imageResponse = new ImageResponse();
        Optional<Image> imageToCreate = imageRepository.findImageByFileName(createImageRequest.getFileName());
        if(imageToCreate.isEmpty()) {
            imageResponse.setStatusCode(401);
            imageResponse.setMessage("Image with file name " + createImageRequest.getFileName() +
                    "cannot be created");
            return imageResponse;
        }
        Image newImage = Image.builder()
                .fileName(createImageRequest.getFileName())
                .fileType(createImageRequest.getFileType())
                .image(createImageRequest.getImage())
                .image(createImageRequest.getImage())
                .downloadUrl(createImageRequest.getDownloadUrl())
                .UUID(UUID.randomUUID().toString())
                .build();
//      /
        return imageResponse;
    }

    @Override
    public ImageResponse getImageByFileName(String fileName) {
        ImageResponse imageResponse = new ImageResponse();
        Optional<Image> image =imageRepository.findImageByFileName(fileName);
        if(image.isEmpty()){
            imageResponse.setStatusCode(404);
            imageResponse.setMessage("Image Not Found");
            return imageResponse;
        }
        imageResponse.setStatusCode(200);
        imageResponse.setMessage("Image Found with file name " + fileName + " found successfully!");
        return imageResponse;
    }

    @Override
    public ImageResponse getImageByUrl(String url) {
        ImageResponse imageResponse = new ImageResponse();
        Optional<Image> optionalImage = imageRepository.findImageByDownloadUrl(url);
        if(optionalImage.isEmpty()){
            imageResponse.setStatusCode(404);
            imageResponse.setMessage("Image with url " + url + " Not Found");
            return imageResponse;
        }
        imageResponse.setStatusCode(200);
        imageResponse.setMessage("Image with download url " + url + " found successfully!");
        return imageResponse;
    }

    @Override
    public ImageResponse getImageByName(String name) {
        return null;
    }

    @Override
    public ImageResponse deleteImage(Long id) {
        ImageResponse imageResponse = new ImageResponse();
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete,
                ()-> {throw new RuntimeException("Image Not Found");
        });
        imageResponse.setStatusCode(200);
        imageResponse.setMessage("Image Deleted");
        return imageResponse;
    }

    @Override
    public ImageResponse deleteImageByUrl(String url) {
        ImageResponse imageResponse = new ImageResponse();
        Optional<Image> newImage = imageRepository.findImageByDownloadUrl(url);
        if(newImage.isEmpty()) {
            imageResponse.setStatusCode(404);
            imageResponse.setMessage("Image Not Found");
            return imageResponse;
        }
        imageRepository.delete(newImage.get());
        imageResponse.setStatusCode(200);
        imageResponse.setMessage("Image deleted successfully!");
        return imageResponse;
    }

    @Override
    public ImageResponse updateImage(MultipartFile file, Long id) {
        ImageResponse imageResponse = new ImageResponse();
        try {
            Image image = imageRepository.findById(id).orElseThrow(()
                    -> new RuntimeException("Image Not Found,cannot be updated"));
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return imageResponse;
    }
}
