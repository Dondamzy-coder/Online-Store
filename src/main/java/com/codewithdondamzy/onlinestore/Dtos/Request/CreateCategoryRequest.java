package com.codewithdondamzy.onlinestore.Dtos.Request;

import com.codewithdondamzy.onlinestore.Models.Products;
import lombok.Data;

import java.util.List;

@Data
public class CreateCategoryRequest {
    private String name;
    private List<Products> productsList;
}
