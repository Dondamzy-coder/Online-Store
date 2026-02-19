package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateImageRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.ImageResponse;
import com.codewithdondamzy.onlinestore.Models.Image;
import com.codewithdondamzy.onlinestore.Service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/OnlineStore")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/createImage")
    public ResponseEntity<?> createImage(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.createImage(file));
    }

    @PostMapping("/saveImages")
    public ResponseEntity<?> saveImages(@RequestParam List<MultipartFile> files, @RequestParam Long productId) {
        return ResponseEntity.ok(imageService.saveImages(files, productId));
    }

    @GetMapping("/getImageByFileName")
    public ResponseEntity<?> getImageByFileName(@RequestParam("fileName") String fileName) {
        return ResponseEntity.ok(imageService.getImageByFileName(fileName));
    }

    @GetMapping("/getImageByUrl")
    public ResponseEntity<?> getImageByUrl(@RequestParam("url") String url) {
        return ResponseEntity.ok(imageService.getImageByUrl(url));
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
