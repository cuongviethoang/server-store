package com.project.ensureQuality.security.services;


import com.project.ensureQuality.model.User;
import com.project.ensureQuality.payload.response.MessageResponse;

import java.util.List;

public interface UserService {

    MessageResponse addNewUser(User user);

    List<User> getUsersWithPagination(int page, int limit);

    Boolean updateUser(User user);

    Boolean deleteUser(int userId);
}
