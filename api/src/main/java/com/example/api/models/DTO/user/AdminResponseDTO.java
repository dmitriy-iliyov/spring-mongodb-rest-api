package com.example.api.models.DTO.user;

import com.example.api.models.Role;
import com.example.api.models.entitys.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminResponseDTO {

    private Long id;
    private String name;
    private String password;
    private Role role;

    public static AdminResponseDTO toDTO(UserEntity userEntity){
        return new AdminResponseDTO(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getPassword(),
                userEntity.getRole()
        );
    }
}
