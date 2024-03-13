package com.project.ensureQuality.controller;

import com.project.ensureQuality.model.ERole;
import com.project.ensureQuality.model.Role;
import com.project.ensureQuality.model.User;
import com.project.ensureQuality.payload.request.LoginRequest;
import com.project.ensureQuality.payload.request.SignupRequest;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.payload.response.UserInfoResponse;
import com.project.ensureQuality.repository.RoleRepository;
import com.project.ensureQuality.repository.UserRepository;
import com.project.ensureQuality.security.jwt.JwtUtils;
import com.project.ensureQuality.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/auth/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        try {
            if(loginRequest.getValueLogin().length() == 0 || loginRequest.getPassword().length() == 0) {
                return  ResponseEntity.status(400).body(new MessageResponse("Lỗi: Vui lòng điền đầy đủ thông tin", -1));
            }
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getValueLogin(), loginRequest.getPassword()));

            // lưu authentication vào SecurityContextHolder để mỗi lần gửi request xuống server đều có đối tượng Authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                        .body(new UserInfoResponse(userDetails.getId(),
                                userDetails.getUsername(),
                                userDetails.getEmail(),
                                userDetails.getPhoneNumber(),
                                roles
                        ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(500).body(new MessageResponse("Lôi: Tài khoản mật khẩu không chính xác", -1));
        }
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
            try {
                if ( signUpRequest.getEmail().length() == 0 ) {
                    return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Email là yêu cầu bắt buộc", 1));
                }
                if ( signUpRequest.getPhoneNumber().length() == 0  ) {
                    return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Phone là yêu cầu bắt buộc", 1));
                }
                if ( signUpRequest.getPassword().length() == 0) {
                    return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Password là yêu cầu bắt buộc", 1));
                }
                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                    return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Email đã tồn taị", 1));
                }
                if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
                    return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Số điện thoại đã tồn tại", 1));
                }
                if (signUpRequest.getPassword().length() < 6) {
                    return ResponseEntity.status(400).body(new MessageResponse("Lỗi: Password phải từ 6 kí tự trở lên", 1));
                }

                Date date = new Date();

                User user = new User(
                        signUpRequest.getEmail(),
                        signUpRequest.getPhoneNumber(),
                        encoder.encode(signUpRequest.getPassword()),
                        signUpRequest.getUsername(),
                        date,date
                        );

                List<String> strRoles = signUpRequest.getRoles();
                List<Role> roles = new ArrayList<>();

                if(strRoles == null) {
                    Role userRole = roleRepository.findByName(ERole.ROLE_STAFF)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                } else {
                    strRoles.forEach(role -> {
                        switch (role) {
                            case "manager":
                                Role adminRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(adminRole);

                                break;
                            default:
                                Role userRole = roleRepository.findByName(ERole.ROLE_STAFF)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                roles.add(userRole);
                        }
                    });
                }
                user.setRoles(roles);
                userRepository.save(user);
                return ResponseEntity.ok(new MessageResponse("Đăng kí thành công!", 0));
            } catch (Exception e) {
                System.out.println(e);
                return ResponseEntity.status(500).body(new MessageResponse("Error server", -1));
            }

    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Đăng xuất thành công!", 0));
    }

}
