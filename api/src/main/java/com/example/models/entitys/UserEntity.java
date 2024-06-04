package com.example.models.entitys;

import com.example.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="users")
public class UserEntity {

    @Id
    private String id;
    private String name;
    private String password;
    private String email;
    private Instant createDate;
    private Role role;
    private List<PostEntity> posts;

}
