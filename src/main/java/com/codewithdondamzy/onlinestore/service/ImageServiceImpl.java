package com.codewithdondamzy.onlinestore.service;

import com.codewithdondamzy.onlinestore.Dtos.Response.ImageResponse;
import com.codewithdondamzy.onlinestore.Models.Image;
import com.codewithdondamzy.onlinestore.Models.Products;
import com.codewithdondamzy.onlinestore.Repository.ImageRepository;
import com.codewithdondamzy.onlinestore.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ProductService  productService;
    private final ProductRepository productRepository;

    public ImageServiceImpl(ImageRepository imageRepository, ProductService productService, ProductRepository productRepository) {
        this.imageRepository = imageRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }
    @Override
    public ImageResponse createImage(MultipartFile file) throws IOException {
        ImageResponse imageResponse = new ImageResponse();
        Optional<Image> imageToCreate = imageRepository.findImageByFileName(file.getOriginalFilename());
        if(imageToCreate.isPresent()) {
            imageResponse.setStatusCode(401);
            imageResponse.setMessage("Image with file name " + file.getOriginalFilename() +
                    "already exists");
            return imageResponse;
        }
        Image newImage = Image.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .image(file.getBytes())
                .UUID(UUID.randomUUID().toString())
                .build();
        String buildDownloadUrl = "/OnlineStore/images/image/download/";
        String downloadUrl = buildDownloadUrl+newImage.getUUID();
        newImage.setDownloadUrl(downloadUrl);
        imageRepository.save(newImage);
        imageResponse.setStatusCode(200);
        imageResponse.setMessage("Image created successfully");
        imageResponse.setData(newImage);
        return imageResponse;
    }

    @Override
    public ImageResponse getImageById(Long id) {
        ImageResponse imageResponse = new ImageResponse();
        Optional<Image> image = imageRepository.findById(id);
        if(image.isEmpty()) {
            imageResponse.setStatusCode(404);
            imageResponse.setMessage("Image with id " + id + " not found");
            return imageResponse;
        }
        Image newImage = image.get();
        imageResponse.setStatusCode(200);
        imageResponse.setMessage("Image with id " + id + "gotten successfully!");
        return imageResponse;
    }

    @Override
    public ImageResponse saveImages(List<MultipartFile> files, Long productId) {
        ImageResponse imageResponse = new ImageResponse();
        Optional<Products> products = productRepository.findProductsById(productId);
        if(products.isEmpty()) {
            imageResponse.setStatusCode(400);
            imageResponse.setMessage("Product with id " + productId + " not found, cannot save image");
            return imageResponse;
        }
        Products productToSaveImage = products.get();
        List<Image> newProductImages = new ArrayList<>();
        for(MultipartFile file : files) {
            try {
                Image newImage = Image.builder()
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .image(file.getBytes())
                        .product(productToSaveImage)
                        .UUID(UUID.randomUUID().toString())
                        .build();
                imageRepository.save(newImage);
                String buildDownloadUrl = "/OnlineStore/images/image/download/";
                String downloadUrl = buildDownloadUrl+newImage.getUUID();
                newImage.setDownloadUrl(downloadUrl);
                imageRepository.saveAll(newProductImages);

                newProductImages.add(newImage);

                imageResponse.setStatusCode(200);
                imageResponse.setMessage("Images saved successfully");
                return imageResponse;
            } catch (IOException e) {
                e.printStackTrace();
                imageResponse.setStatusCode(500);
                imageResponse.setMessage("Image saving failed,please try again later");
                return imageResponse;
            }
        }
        return imageResponse;
    }

    @Override
    public ImageResponse getImageByFileName(String fileName) {
        ImageResponse imageResponse = new ImageResponse();
        try {
            Optional<Image> image =imageRepository.findImageByFileName(fileName);
            if(image.isEmpty()){
                imageResponse.setStatusCode(404);
                imageResponse.setMessage("Image Not Found");
                return imageResponse;
            }
            imageResponse.setStatusCode(200);
            imageResponse.setMessage("Image Found with file name " + fileName + " found successfully!");
            return imageResponse;
        } catch (Exception e) {
            imageResponse.setStatusCode(500);
            imageResponse.setMessage("Internal Server Error");
            return imageResponse;
        }
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
            Optional<Image> image = imageRepository.findById(id);
            if(image.isEmpty()) {
                imageResponse.setStatusCode(404);
                imageResponse.setMessage("Image with id " + id + " not found");
                return imageResponse;
            }
          Image imageToUpdate = image.get();
           imageToUpdate.setFileName(file.getOriginalFilename());
            imageToUpdate.setFileType(file.getContentType());
            imageToUpdate.setImage(file.getBytes());
            imageRepository.save(imageToUpdate);
            imageResponse.setStatusCode(200);
            imageResponse.setMessage("Image with id " + id + " updated successfully");
            return imageResponse;
        } catch (IOException e) {
            imageResponse.setStatusCode(500);
            imageResponse.setMessage("Internal Server Error");
            return imageResponse;
        }
    }
}
