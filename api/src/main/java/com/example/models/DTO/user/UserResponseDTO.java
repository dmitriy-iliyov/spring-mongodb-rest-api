package com.example.api.models.DTO.user;


import com.example.api.models.DTO.post.PostResponseDTO;
import com.example.api.models.Role;
import com.example.api.models.entitys.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String name;
    private Role role;
    private String password;
    private String email;
    private Instant createDate;
    private List<PostResponseDTO> posts;


    public static UserResponseDTO toDTO(UserEntity userEntity){
        List<PostResponseDTO> listOfPostDTO = new ArrayList<>();
        userEntity.getPosts().forEach(postEntity -> listOfPostDTO.add(PostResponseDTO.toDTO(postEntity)));

        return new UserResponseDTO(
                userEntity.getId(), userEntity.getName(), userEntity.getRole(), userEntity.getPassword(),
                userEntity.getEmail(), userEntity.getCreateDate(), listOfPostDTO
        );
    }
}
