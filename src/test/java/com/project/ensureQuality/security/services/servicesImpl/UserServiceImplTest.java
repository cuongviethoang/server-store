package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.ERole;
import com.project.ensureQuality.model.Role;
import com.project.ensureQuality.model.User;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.repository.RoleRepository;
import com.project.ensureQuality.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    public UserServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNewUser_Success() {
        Date date = new Date();
        User user = new User("test@gmail.com", "0981234765", "password", "Tuan", date, date);
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(ERole.ROLE_STAFF));
        user.setRoles(roles);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(false);
//        when(roleRepository.findByName(ERole.ROLE_STAFF)).thenReturn(Optional.of(new Role(ERole.ROLE_STAFF)));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);

        MessageResponse response = userService.addNewUser(user);
        assertNotNull("Response is not null", response);
        assertEquals("Tạo mới user thành công", response.getEM());
        assertEquals(0, response.getEC());
    }

    @Test
    public void testAddNewUser_Fail_EmailExists() {
        Date date = new Date();
        User user = new User("test@gmail.com", "0981234765", "password", "Tuan", date, date);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        MessageResponse response = userService.addNewUser(user);

        assertNotNull("Response is not null", response);
        assertEquals("Lỗi: Email đã tồn tại", response.getEM());
        assertEquals(1, response.getEC());
    }

    @Test
    public void testAddNewUser_Fail_EmailIsEmpty() {
        Date date = new Date();
        User user = new User("", "0981234765", "password", "Tuan", date, date);

        MessageResponse response = userService.addNewUser(user);

        assertNotNull("Response is not null", response);
        assertEquals("Lỗi: Email là yêu cầu bắt buộc", response.getEM());
        assertEquals(1, response.getEC());
    }

    @Test
    public void testAddNewUser_Fail_PhoneIsEmpty() {
        Date date = new Date();
        User user = new User("test@gmail.com", "", "password", "Tuan", date, date);

        MessageResponse response = userService.addNewUser(user);

        assertNotNull("Response is not null", response);
        assertEquals("Lỗi: Phone là yêu cầu bắt buộc", response.getEM());
        assertEquals(1, response.getEC());
    }

    @Test
    public void testAddNewUser_Fail_PasswordIsEmpty() {
        Date date = new Date();
        User user = new User("test@gmail.com", "0912345868", "", "Tuan", date, date);

        MessageResponse response = userService.addNewUser(user);

        assertNotNull("Response is not null", response);
        assertEquals("Lỗi: Password là yêu cầu bắt buộc", response.getEM());
        assertEquals(1, response.getEC());
    }

    @Test
    public void testGetUsersWithPagination() {
        int page = 1;
        int limit = 10;

        List<User> mockUsers = new ArrayList<>();
        Date date = new Date();
        mockUsers.add(new User("test@gmail.com", "0981234765", "password", "Tuan", date, date));
        mockUsers.add(new User("test2@gmail.com", "0981234787", "password", "Nam", date, date));

        when(userRepository.getUserWithPagination(0, 10)).thenReturn(mockUsers);

        List<User> users = userService.getUsersWithPagination(page, limit);

        assertNotNull("Users is not null", users);
        assertEquals(2, users.size());
    }

    @Test
    public void testAddNewUser_InvalidPasswordLength() {
        Date date = new Date();
        User user = new User("test@gmail.com", "0981234765", "pass", "Tuan", date, date);

        MessageResponse response = userService.addNewUser(user);
        assertNotNull("Response is not null", response);
        assertEquals("Lỗi: Password phải từ 6 kí tự trở lên", response.getEM());
        assertEquals(1, response.getEC());
    }

    @Test
    public void testAddNewUser_ExistingPhoneNumber() {
        Date date = new Date();
        User user = new User("email@gmail.com", "0981234765", "password", "Tuan", date, date);

        when(userRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);

        MessageResponse response = userService.addNewUser(user);

        assertNotNull("Response is not null", response);
        assertEquals("Lỗi: Số điện thoại đã tồn tại", response.getEM());
        assertEquals(1, response.getEC());
    }

    @Test
    public void testDeleteUser_Success() {
        int userIdToDelete = 1;
        doNothing().when(userRepository).deleteById(userIdToDelete);

        boolean isDelete = userService.deleteUser(userIdToDelete);

        assertTrue(isDelete);
        verify(userRepository, times(1)).deleteById(userIdToDelete);
    }

    @Test void testDeleteUser_Fail() {
        int nonExistingUserId = 1000;

        doThrow(new RuntimeException("User not found")).when(userRepository).deleteById(nonExistingUserId);
        boolean isDeleted = userService.deleteUser(nonExistingUserId);
        assertFalse(isDeleted);
    }

    @Test void testGetAllUserNum() {
        List<User> users = new ArrayList<>();
        Date date = new Date();
        users.add(new User("test@gmail.com", "0981234765", "password", "Tuan", date, date));
        users.add(new User("test2@gmail.com", "0981234787", "password", "Nam", date, date));
        when(userRepository.findAll()).thenReturn(users);

        int totalUsers = userService.getAllUserNum();
        assertEquals(2, totalUsers);
    }
}
