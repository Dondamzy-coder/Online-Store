package com.codewithdondamzy.onlinestore.Dtos.Response;

import com.codewithdondamzy.onlinestore.Models.Category;
import lombok.Data;

@Data
public class CategoryResponse {
    private int statusCode;
    private String message;
    private Object data;

}
