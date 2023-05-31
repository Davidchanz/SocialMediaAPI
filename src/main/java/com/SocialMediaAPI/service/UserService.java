package com.SocialMediaAPI.service;

import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }

    private boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private String encryptPassword(String password){
        return new BCryptPasswordEncoder().encode(password);
    }

    public Optional<User> findUserByUserName(String userName){
        return userRepository.findByUsername(userName);
    }

    /*private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }*/
}
