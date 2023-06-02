package com.SocialMediaAPI.service;

import com.SocialMediaAPI.exception.UserAlreadyExistException;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.Optional;

@Transactional
@Service
public class UserService implements UserDetailsService {
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

    public User findUserByUserName(String userName){
        return userRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException("Could not found a user with given name"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User loadedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not found a user with given name"));
        return new org.springframework.security.core.userdetails.User(
                loadedUser.getUsername(),
                loadedUser.getPassword(),
                loadedUser.isEnabled(),
                loadedUser.isAccountNonExpired(),
                loadedUser.isCredentialsNonExpired(),
                loadedUser.isAccountNonLocked(),
                Collections.singleton(new SimpleGrantedAuthority("read"))//TODO
        );
    }

    public User registerNewUserAccount(User user) throws UserAlreadyExistException {
        if (userExists(user.getUsername())) {
            throw new UserAlreadyExistException("User with username: '"
                    + user.getUsername() + "' is already exist!");
        }

        user.setPassword(encryptPassword(user.getPassword()));

        //user.setRoles(List.of("ROLE_USER"));

        return userRepository.save(user);
    }
}
