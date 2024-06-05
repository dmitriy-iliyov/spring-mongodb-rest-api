package com.example.models.entitys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="posts")
public class PostEntity {

    @Id
    private String id;
    private String topic;
    private String description;
    private Instant createDate;
    private UserEntity user;
    private CategoryEntity category;

}
