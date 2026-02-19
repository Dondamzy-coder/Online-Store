package com.codewithdondamzy.onlinestore.Dtos.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;

@Data
public class CreateImageRequest {
    private MultipartFile file;
}
