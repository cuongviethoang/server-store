package com.project.ensureQuality.controller;

import com.project.ensureQuality.model.User;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.payload.response.UserInfoResponse;
import com.project.ensureQuality.security.services.UserDetailsImpl;
import com.project.ensureQuality.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/user/read")
    public ResponseEntity<?> getUsersWithPagination(@RequestParam int page, @RequestParam int limit) {
        try {
            List<User> users = userService.getUsersWithPagination(page, limit);
            return ResponseEntity.status(200).body(users);
        } catch (Exception e){
            return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/user/create")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        try {
            MessageResponse messageResponse = userService.addNewUser(user);
            if(messageResponse.getEC() == 0){
                return ResponseEntity.status(200).body(messageResponse);
            }
            return ResponseEntity.status(400).body(messageResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/user/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "userId") int userId) {
        try {
            Boolean check = userService.deleteUser(userId);
            if(check == true){
                return ResponseEntity.status(200).body(new MessageResponse("Xóa user thành công", 0));
            }
            return ResponseEntity.status(400).body(new MessageResponse("Xóa user thất bai. Vui lòng thử lại!", 1));
        } catch (Exception e){
            return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
        }
    }

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/user/account")
    public ResponseEntity<?> getUserAccount(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.status(200).body(new UserInfoResponse(userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getPhoneNumber(),
                    roles
            ));

        } catch (Exception e) {
            ResponseEntity.status(500).body(new MessageResponse("Tải thông tin người dùng thất bại", -1));
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
