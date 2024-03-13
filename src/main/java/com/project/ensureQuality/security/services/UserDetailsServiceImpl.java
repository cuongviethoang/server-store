package com.project.ensureQuality.security.services;

import com.project.ensureQuality.model.User;
import com.project.ensureQuality.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String valueLogin) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrPhoneNumber(valueLogin)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with valueLogin: " + valueLogin));

        return UserDetailsImpl.build(user);
    }
}
