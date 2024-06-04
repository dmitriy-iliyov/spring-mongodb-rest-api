package com.example.api.models.DTO.category;

import com.example.api.models.entitys.CategoryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryCreatingDTO {

    private String name;

    public static CategoryEntity toEntity(CategoryCreatingDTO categoryDTO){
        return new CategoryEntity(categoryDTO.getName());
    }
}
