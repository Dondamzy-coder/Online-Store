package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/createImage")
    public ResponseEntity<?> createImage(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageService.createImage(file));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/saveImages")
    public ResponseEntity<?> saveImages(@RequestParam("files") List<MultipartFile> files, @RequestParam Long productId) {
        return ResponseEntity.ok(imageService.saveImages(files, productId));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getImageById/{id}")
    public ResponseEntity<?> getImageById(@PathVariable Long id){
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @GetMapping("/getImageByFileName")
    public ResponseEntity<?> getImageByFileName(@RequestParam("fileName") String fileName) {
        return ResponseEntity.ok(imageService.getImageByFileName(fileName));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/getImageByUrl")
    public ResponseEntity<?> getImageByUrl(@RequestParam("url") String url) {
        return ResponseEntity.ok(imageService.getImageByUrl(url));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteImageById/{id}")
    public ResponseEntity<?> deleteImageById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.deleteImage(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteImageByUrl")
    public ResponseEntity<?> deleteImageByUrl(@RequestParam("url") String url) {
        return ResponseEntity.ok(imageService.deleteImageByUrl(url));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateProductImage/{id}")
    public ResponseEntity<?> updateImage(@PathVariable Long id,@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(imageService.updateImage(file,id));
    }
}
