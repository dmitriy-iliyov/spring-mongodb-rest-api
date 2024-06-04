package com.example.models;

import lombok.Data;

@Data
public class SearchRequest {

    private Long userID;
    private String userName;
    private Long categoryID;
    private String categoryName;

}
