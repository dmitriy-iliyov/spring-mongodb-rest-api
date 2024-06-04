package com.example.controllers;

import com.example.models.DTO.user.AdminResponseDTO;
import com.example.models.DTO.user.UserLogInDTO;
import com.example.models.DTO.user.UserResponseDTO;
import com.example.models.DTO.user.UserRegistrationDTO;
import com.example.models.Role;
import com.example.models.entitys.UserEntity;
import com.example.security.JwtCore;
import com.example.api.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;

    @GetMapping("/new")
    public String registerNewUserForm(Model model){
        model.addAttribute("user", new UserRegistrationDTO());

        return "user_register_form";
    }

    @PostMapping("/new")
    public ResponseEntity<String> saveNewUser(@ModelAttribute UserRegistrationDTO user){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Creating user");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try{
            userService.save(UserRegistrationDTO.toEntity(user));
            httpHeaders.setLocation(URI.create("/user/login"));
            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .headers(httpHeaders)
                    .body("User successfully created, redirecting...");
        }catch (DataIntegrityViolationException e){
            System.out.println("EXCEPTION  " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .headers(httpHeaders)
                    .body("User with name " + user.getName() + " already exists");
        }
    }

    @GetMapping("/login")
    public String loggingUserForm(Model model){
        model.addAttribute("user", new UserLogInDTO());

        return "user_signing_in_form";
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@ModelAttribute UserLogInDTO user){

        Authentication authentication;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Authenticate user");

        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));
        } catch (BadCredentialsException e){
            System.out.println("EXCEPTION  " + e.getMessage());
            httpHeaders.add("Error-Message", "Incorrect password, " + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .headers(httpHeaders)
                    .body("Incorrect password or user with " + user.getName() + " name  isn't exist.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(jwt);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public String editUserForm(@PathVariable Long id, Model model){
        UserResponseDTO userResponseDTO = userService.findDtoById(id).orElse(null);
        if(userResponseDTO != null){
            model.addAttribute("user", userResponseDTO);
            return "user_edit_form";
        }
        else{
            System.out.println("EXCEPTION  User not found in database!");
            return "redirect:/user/login";
        }
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String>  saveEditedUser(@ModelAttribute UserResponseDTO userResponseDTO){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-info", "Editing user");

        try {
            userService.update(userResponseDTO, passwordEncoder);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(httpHeaders)
                    .body("Successfully edited");
        } catch (DataIntegrityViolationException e){
            System.out.println("EXCEPTION  " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(httpHeaders)
                    .body("failed(");
        }
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<?> getUserByIdOrName(HttpServletRequest request) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Getting user");

        String jwt = jwtCore.getTokenFromHttpHeader(request.getHeader("Authorization"));
        String name = jwtCore.getNameFromJwt(jwt);
        Long id = jwtCore.getIdFromJwt(jwt);

        try {
            if(name != null)
                return getResponseEntity(httpHeaders, userService.findEntityByName(name));
            if(id != null)
                return getResponseEntity(httpHeaders, userService.findEntityById(id));
        } catch (NullPointerException e){
            System.out.println("EXCEPTION  " + e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .headers(httpHeaders)
                .body(null);
    }

    private ResponseEntity<?> getResponseEntity(HttpHeaders httpHeaders, Optional<UserEntity> userEntity) {
        UserResponseDTO userResponseDTO = userEntity.map(UserResponseDTO::toDTO).orElse(null);
        if(userResponseDTO != null){
            if(userResponseDTO.getRole() == Role.ADMIN)
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .headers(httpHeaders)
                        .body(userEntity.map(AdminResponseDTO::toDTO));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(httpHeaders)
                    .body(userResponseDTO);
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(httpHeaders)
                .body(null);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> users = userService.findAllUsers();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Getting all users");

        return users.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(null)
                : ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(users);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {

        String jwt = jwtCore.getTokenFromHttpHeader(request.getHeader("Authorization"));
        Long id = jwtCore.getIdFromJwt(jwt);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Deleting user by id");

        try {
            userService.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(httpHeaders)
                    .body("User with id " + id + " has been successfully deleted");
        } catch (Exception e) {
            System.out.println("EXCEPTION  " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(httpHeaders)
                    .body("Failed to delete user with id " + id + ": " + e.getMessage());
        }
    }
}
