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
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;


@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    public AuthControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_Success() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("testemail@gmail.com");
        signupRequest.setUsername("username");
        signupRequest.setPassword("password123");
        signupRequest.setPhoneNumber("0987654321");
        signupRequest.setRoles(Collections.singletonList("staff"));
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        Role mockRole = new Role(ERole.ROLE_STAFF);
        when(roleRepository.findByName(ERole.ROLE_STAFF)).thenReturn(Optional.of(mockRole));

        ResponseEntity<?> response = authController.registerUser(signupRequest);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) response.getBody();
        assertEquals("Đăng ký thành công!", responseBody.getEM());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(signupRequest.getEmail(), savedUser.getEmail());
        assertEquals(signupRequest.getUsername(), savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(signupRequest.getPhoneNumber(), savedUser.getPhoneNumber());
        assertEquals(1, savedUser.getRoles().size());
        assertEquals(mockRole, savedUser.getRoles().get(0));
    }

    @Test
    public void registerUser_EmailIsEmpty() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("");
        signupRequest.setUsername("username");
        signupRequest.setPassword("password");
        signupRequest.setPhoneNumber("0123456789");
        signupRequest.setRoles(Collections.singletonList(""));

        ResponseEntity<?> response = authController.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) response.getBody();
        assertEquals("Lỗi: Email là yêu cầu bắt buộc", responseBody.getEM());
    }

    @Test
    public void registerUser_PhoneIsEmpty() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@gmail.com");
        signupRequest.setUsername("username");
        signupRequest.setPassword("password");
        signupRequest.setPhoneNumber("");
        signupRequest.setRoles(Collections.singletonList(""));

        ResponseEntity<?> response = authController.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) response.getBody();
        assertEquals("Lỗi: Phone là yêu cầu bắt buộc", responseBody.getEM());
    }

    @Test
    public void registerUser_PassWordIsEmpty() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@gmail.com");
        signupRequest.setUsername("username");
        signupRequest.setPhoneNumber("0912345678");
        signupRequest.setPassword("");
        signupRequest.setRoles(Collections.singletonList(""));

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())).thenReturn(false);

        ResponseEntity<?> response = authController.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) response.getBody();
        assertEquals("Lỗi: Password là yêu cầu bắt buộc", responseBody.getEM());
    }

    @Test
    public void registerUser_EmailExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("email@gmail.com");
        signupRequest.setUsername("username");
        signupRequest.setPassword("password123");
        signupRequest.setPhoneNumber("0987654321");
        signupRequest.setRoles(null);
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) responseEntity.getBody();
        assertEquals("Lỗi: Email đã tồn tại", responseBody.getEM());
    }

    @Test
    public void registerUser_PhoneExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("mail0812@gmail.com");
        signupRequest.setUsername("username");
        signupRequest.setPassword("password123");
        signupRequest.setPhoneNumber("0987654321");
        signupRequest.setRoles(null);
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())).thenReturn(true);

        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) responseEntity.getBody();
        assertEquals("Lỗi: Số điện thoại đã tồn tại", responseBody.getEM());
    }

    @Test
    public void registerUser_ErrorPassWord() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("mail0812@gmail.com");
        signupRequest.setUsername("username");
        signupRequest.setPassword("pass");
        signupRequest.setPhoneNumber("0987654322");
        signupRequest.setRoles(null);
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())).thenReturn(false);

        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) responseEntity.getBody();
        assertEquals("Lỗi: Password phải từ 6 kí tự trở lên", responseBody.getEM());
    }

    @Test
    public void signInUser_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setValueLogin("email@gmail.com");
        loginRequest.setPassword("password123");

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1, "username", "0987654321", "email@gmail.com", "", Collections.emptyList()
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        ResponseCookie mockCookie = ResponseCookie.from("jwtCookieName", "mockJwtCookieValue")
                .maxAge(Duration.ofHours(1))
                .path("/")
                .build();
        when(jwtUtils.generateJwtCookie(any(UserDetailsImpl.class))).thenReturn(mockCookie);
        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HttpHeaders headers = response.getHeaders();
        assertNotNull("HttpHeaders should not be null", headers);
        assertTrue(headers.containsKey(HttpHeaders.SET_COOKIE));

        Object responseBody = response.getBody();
        assertNotNull("The object should not be null", responseBody);
        assertTrue(responseBody instanceof UserInfoResponse);

        UserInfoResponse userInfoResponse = (UserInfoResponse) responseBody;
        assertEquals(1, userInfoResponse.getId());
        assertEquals("username", userInfoResponse.getUsername());
        assertEquals("email@gmail.com", userInfoResponse.getEmail());
        assertEquals("0987654321", userInfoResponse.getPhoneNumber());
        assertTrue(userInfoResponse.getRoles().isEmpty());
    }

    @Test
    public void signInUser_EmailIsInvalid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setValueLogin("email0812@gmail.com");
        loginRequest.setPassword("password123");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Object responseBody = response.getBody();
        assertNotNull("The response body should not be null", responseBody);
        assertTrue(responseBody instanceof MessageResponse);

        MessageResponse messageResponse = (MessageResponse) responseBody;
        assertEquals("Lỗi: Tài khoản mật khẩu không chính xác", messageResponse.getEM());
    }

    @Test
    public void signInUser_PasswordIsInvalid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setValueLogin("email@gmail.com");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Object responseBody = response.getBody();
        assertNotNull("The response body should not be null", responseBody);
        assertTrue(responseBody instanceof MessageResponse);

        MessageResponse messageResponse = (MessageResponse) responseBody;
        assertEquals("Lỗi: Tài khoản mật khẩu không chính xác", messageResponse.getEM());
    }

    @Test
    public void signInUser_EmailPasswordIsEmpty() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setValueLogin("");
        loginRequest.setPassword("");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Object responseBody = response.getBody();
        assertNotNull("The response body should not be null", responseBody);
        assertTrue(responseBody instanceof MessageResponse);

        MessageResponse messageResponse = (MessageResponse) responseBody;
        assertEquals("Lỗi: Vui lòng điền đầy đủ thông tin", messageResponse.getEM());
    }

    @Test
    public void signInUser_EmailIsEmpty() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setValueLogin("");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Object responseBody = response.getBody();
        assertNotNull("The response body should not be null", responseBody);
        assertTrue(responseBody instanceof MessageResponse);

        MessageResponse messageResponse = (MessageResponse) responseBody;
        assertEquals("Lỗi: Vui lòng điền đầy đủ thông tin", messageResponse.getEM());
    }

    @Test
    public void signInUser_PasswordIsEmpty() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setValueLogin("");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Object responseBody = response.getBody();
        assertNotNull("The response body should not be null", responseBody);
        assertTrue(responseBody instanceof MessageResponse);

        MessageResponse messageResponse = (MessageResponse) responseBody;
        assertEquals("Lỗi: Vui lòng điền đầy đủ thông tin", messageResponse.getEM());
    }

    @Test
    public void logoutUser_Success() {
        ResponseCookie mockCookie = ResponseCookie.from("jwt", "")
                .maxAge(Duration.ZERO)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();
        when(jwtUtils.getCleanJwtCookie()).thenReturn(mockCookie);

        ResponseEntity<?> response = authController.logoutUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse responseBody = (MessageResponse) response.getBody();
        assertEquals("Đăng xuất thành công!", responseBody.getEM());
        assertEquals(0, responseBody.getEC());

        HttpHeaders headers = response.getHeaders();
        List<String> setCookieHeaders = headers.get(HttpHeaders.SET_COOKIE);
        assertNotNull("Not null", setCookieHeaders);
        assertEquals(1, setCookieHeaders.size());
        assertEquals(mockCookie.toString(), setCookieHeaders.get(0));
    }
}
