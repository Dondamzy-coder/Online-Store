package com.codewithdondamzy.onlinestore.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id@GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long imageId;
    private String fileName;
    private String fileType;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    private String downloadUrl;
    private String UUID;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;
}
