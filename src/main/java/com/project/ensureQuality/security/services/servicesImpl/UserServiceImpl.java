package com.project.ensureQuality.security.services.servicesImpl;

import com.project.ensureQuality.model.ERole;
import com.project.ensureQuality.model.Role;
import com.project.ensureQuality.model.User;
import com.project.ensureQuality.payload.response.MessageResponse;
import com.project.ensureQuality.repository.RoleRepository;
import com.project.ensureQuality.repository.UserRepository;
import com.project.ensureQuality.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public MessageResponse addNewUser(User user) {
        try {
            if ( user.getEmail().length() == 0 ) {
                return new MessageResponse("Lỗi: Email là yêu cầu bắt buộc", 1);
            }
            if ( user.getPhoneNumber().length() == 0  ) {
                return new MessageResponse("Lỗi: Phone là yêu cầu bắt buộc", 1);
            }
            if ( user.getPassword().length() == 0) {
                return new MessageResponse("Lỗi: Password là yêu cầu bắt buộc", 1);
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                return new MessageResponse("Lỗi: Email đã tồn taị", 1);
            }
            if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
                return new MessageResponse("Lỗi: Số điện thoại đã tồn tại", 1);
            }
            if (user.getPassword().length() < 6) {
                return new MessageResponse("Lỗi: Password phải từ 6 kí tự trở lên", 1);
            }
            user.setPassword(encoder.encode(user.getPassword()));
            Date date = new Date();
            user.setCreatedAt(date);
            user.setUpdatedAt(date);

            List<Role> roles = user.getRoles();
            if(roles.isEmpty()){
                Role userRole = roleRepository.findByName(ERole.ROLE_STAFF)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
                user.setRoles(roles);
            }

            userRepository.save(user);
            return new MessageResponse("Tạo mới user thành công", 0);

        } catch (Exception e) {
            return new MessageResponse("Đã xảy ra lỗi khi thêm user.Vui lòng thử lại!", 1);
        }
    }

    @Override
    public List<User> getUsersWithPagination(int page, int limit) {
        int offset = (page-1) * limit;
        List<User> users = userRepository.getUserWithPagination(offset, limit);
        return users;
    }

    @Override
    public Boolean updateUser(User user) {
        return null;
    }

    @Override
    public Boolean deleteUser(int userId) {
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
