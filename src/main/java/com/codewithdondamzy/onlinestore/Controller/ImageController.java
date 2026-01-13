package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateImageRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.ImageResponse;
import com.codewithdondamzy.onlinestore.Models.Image;
import com.codewithdondamzy.onlinestore.Service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/OnlineStore")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @PostMapping("/createImage")
    public ResponseEntity<?> createImage(@RequestBody CreateImageRequest createImageRequest) {
        ImageResponse image = imageService.createImage(createImageRequest);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/getImageByFileName")
    public ResponseEntity<?> getImageByFileName(@RequestParam("fileName") String fileName) {
        return new ResponseEntity<>(imageService.getImageByFileName(fileName), HttpStatus.OK);
    }

    @GetMapping("/getImageByUrl")
    public ResponseEntity<?> getImageByUrl(@RequestParam("url") String url) {
        return new ResponseEntity<>(imageService.getImageByUrl(url), HttpStatus.OK);
    }

    @GetMapping("/getImageByProductName")
    public ResponseEntity<?> getImageByName(@RequestParam("productName") String productName) {
        return new ResponseEntity<>(imageService.getImageByName(productName), HttpStatus.OK);
    }

    @DeleteMapping("/deleteImageById/{id}")
    public ResponseEntity<?> deleteImageById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.deleteImage(id));
    }

    @DeleteMapping("/deleteImageByUrl")
    public ResponseEntity<?> deleteImageByUrl(@RequestParam("url") String url) {
        return ResponseEntity.ok(imageService.deleteImageByUrl(url));
    }

    @PutMapping("/updateProductImage/{id}")
    public ResponseEntity<?> updateImage(@PathVariable Long id, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(imageService.updateImage(file, id));
    }
}
