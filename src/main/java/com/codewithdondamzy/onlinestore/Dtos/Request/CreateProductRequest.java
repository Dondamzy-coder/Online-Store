package com.codewithdondamzy.onlinestore.Dtos.Request;

import com.codewithdondamzy.onlinestore.Models.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {
    private String name;
    private BigDecimal price;
    private String description;
    private int stockQuantity;
    private CreateCategoryRequest category;
    private List<CreateImageRequest> imageRequestList;
    private String UUID;
    private String brand;
    private boolean isAvailable;
}
