package com.example.api.repositorys;

import com.example.api.models.Role;
import com.example.api.models.entitys.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByName(String name);

    boolean existsUserEntityByName(String name);

    Iterable<UserEntity> findAllByRole(Role role);

    void deleteByNameAndPassword(String name, String password);
}
