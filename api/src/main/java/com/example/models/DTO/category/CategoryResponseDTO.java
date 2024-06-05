package com.example.models.DTO.category;

import com.example.models.entitys.CategoryEntity;
import com.example.models.entitys.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryResponseDTO {

    private String id;
    private String name;
    private List<PostEntity> posts;

    public static CategoryResponseDTO toDTO(CategoryEntity categoryEntity){
        return new CategoryResponseDTO(categoryEntity.getId(), categoryEntity.getName(), categoryEntity.getPosts());
    }
}
