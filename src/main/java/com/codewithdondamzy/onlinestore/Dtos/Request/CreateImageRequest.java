package com.codewithdondamzy.onlinestore.Dtos.Request;

import lombok.Data;

import java.sql.Blob;

@Data
public class CreateImageRequest {
    private String fileName;
    private String fileType;
    private Blob image;
    private String downloadUrl;
    private String UUID;
}
