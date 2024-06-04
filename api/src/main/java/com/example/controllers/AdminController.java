package com.example.controllers;

import com.example.models.DTO.user.AdminRegistrationDTO;
import com.example.models.DTO.user.AdminResponseDTO;
import com.example.models.DTO.user.UserLogInDTO;
import com.example.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String registerNewAdmin(Model model) {
        model.addAttribute("admin", new AdminRegistrationDTO());

        return "admin_register_form";
    }

    @PostMapping("/new")
    public ResponseEntity<String> saveNewAdmin(@ModelAttribute AdminRegistrationDTO admin) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Creating admin");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        try{
            userService.save(AdminRegistrationDTO.toEntity(admin));
            httpHeaders.setLocation(URI.create("/user/login"));
            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .body("Admin successfully created, redirecting...");
        } catch(DataIntegrityViolationException e){
            System.out.println("EXCEPTION  " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .headers(httpHeaders)
                    .body("Admin with name " + admin.getName() + " already exists");
        }
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {

        Optional<AdminResponseDTO> adminOptional = userService.findEntityById(id).map(AdminResponseDTO::toDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Getting admin by id");

        return adminOptional
                .map(adminDTO -> ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(adminDTO))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(null));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Iterable<AdminResponseDTO>> getAllAdmins() {

        List<AdminResponseDTO> admins = userService.findAllAdmins();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info", "Getting all admin");

        return admins.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).headers(httpHeaders).body(null)
                : ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(admins);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteAdmin(@RequestBody UserLogInDTO admin ) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Info","Deleting admin by password");
        String adminName = admin.getName();

        try {
            userService.deleteByNameAndPassword(adminName, passwordEncoder.encode(admin.getPassword()));
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .headers(httpHeaders)
                    .body("Admin with name " + adminName + " has been successfully deleted");
        } catch (Exception e) {
            System.out.println("EXCEPTION  " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(httpHeaders)
                    .body("Failed to delete admin with name " + adminName);
        }
    }
}
