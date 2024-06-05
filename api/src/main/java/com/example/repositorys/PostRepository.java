package com.example.repositorys;

import com.example.models.entitys.PostEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<PostEntity, String> {

    Iterable<PostEntity> findAllByUserIdOrUserNameOrCategoryIdOrCategoryName(
            String user_id,
            String user_name,
            String category_id,
            String category_name);

}
