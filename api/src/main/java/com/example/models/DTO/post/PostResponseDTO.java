package com.example.models.DTO.post;

import com.example.models.entitys.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostResponseDTO {

    private Long id;
    private String topic;
    private String description;
    private Long userID;
    private Long categoryID;

    public static PostResponseDTO toDTO(PostEntity postEntity){
        return new PostResponseDTO(
                postEntity.getId(), postEntity.getTopic(), postEntity.getDescription(),
                postEntity.getUser().getId(), postEntity.getCategory().getId()
        );
    }
}
