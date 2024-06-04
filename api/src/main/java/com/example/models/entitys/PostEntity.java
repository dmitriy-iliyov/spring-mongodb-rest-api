package com.example.models.entitys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="posts")
public class PostEntity {

    @Id
    private String id;
    private String topic;
    private String description;
    private UserEntity user;
    private CategoryEntity category;

    public PostEntity(String topic, String description, UserEntity user, CategoryEntity category){
        this.topic = topic;
        this.description = description;
        this.user = user;
        this.category = category;
    }
}
