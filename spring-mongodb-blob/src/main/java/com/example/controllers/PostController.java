package com.example.controllers;

import com.example.models.DTO.post.PostCreatingDTO;
import com.example.models.DTO.post.PostResponseDTO;
import com.example.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
public class PostController {

    private final PostService postService;


    @GetMapping("/new")
    @PreAuthorize("hasAuthority('USER')")
    public String addNewPostForm(Model model) {
        model.addAttribute("post", new PostCreatingDTO());

        return "post_register_form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> saveNewPost(@ModelAttribute PostCreatingDTO post) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-info", "Creating post");

        try {
             postService.save(post);
             return ResponseEntity
                     .status(HttpStatus.CREATED)
                     .headers(httpHeaders)
                     .body("Post successfully created");
        } catch (Exception e){
            System.out.println("EXCEPTION  " + e.getMessage());
            return ResponseEntity
                     .status(HttpStatus.NOT_FOUND)
                     .headers(httpHeaders)
                     .body("User or category doesn't exist");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable String id){

        Optional<PostResponseDTO> postOptional = postService.findById(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Getting post by id");

        return postOptional
                .map(postDTO -> ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(postDTO))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(null));
    }

    @GetMapping("/get")
    public ResponseEntity<List<PostResponseDTO>> findAllByUserIdOrCategoryId(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String categoryId){

        List<PostResponseDTO> posts = postService.findAllByUserIdOrCategoryId(userId, categoryId);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-info", "Getting all post by user or category id or name");

        return posts.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(null)
                : ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(posts);

    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts(){

        List<PostResponseDTO> posts = postService.findAll();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-info", "Getting all posts");

        return posts.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(null)
                : ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(posts);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable String id) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-info", "Deleting post by id");

        try {
            postService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .headers(httpHeaders)
                    .body("Post with id " + id + " has been successfully deleted");
        } catch (Exception e) {
            System.out.println("EXCEPTION  " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(httpHeaders)
                    .body("Failed to delete post with id " + id);
        }
    }
}
