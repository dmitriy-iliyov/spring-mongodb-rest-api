package com.example.api.repositorys;

import com.example.api.models.entitys.PostEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<PostEntity, Long> {

    Iterable<PostEntity> findAllByUserIdOrUserNameOrCategoryIdOrCategoryName(
            Long user_id,
            String user_name,
            Long category_id,
            String category_name);

}
