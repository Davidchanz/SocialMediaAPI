package com.SocialMediaAPI.service;

import com.SocialMediaAPI.exception.EmailAlreadyExistException;
import com.SocialMediaAPI.exception.UserAlreadyExistException;
import com.SocialMediaAPI.model.Chat;
import com.SocialMediaAPI.model.CustomUserDetails;
import com.SocialMediaAPI.model.Notification;
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
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private String encryptPassword(String password){
        return new BCryptPasswordEncoder().encode(password);
    }

    public User findUserByUserName(String userName){
        return userRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException("Could not found a user with given name"));
    }

    public void sendNotification(Notification notification){
        notification.getTo().getNotifications().add(notification);
        userRepository.save(notification.getTo());
    }

    public void registerNewUserAccount(User user) throws UserAlreadyExistException {
        if (userExists(user.getUsername())) {
            throw new UserAlreadyExistException("User with username: '"
                    + user.getUsername() + "' is already exist!");
        }

        if (emailExists(user.getEmail())) {
            throw new EmailAlreadyExistException("User with email: '"
                    + user.getEmail() + "' is already exist!");
        }

        user.setPassword(encryptPassword(user.getPassword()));

        userRepository.save(user);
    }

    public void deleteByUsername(String username){
        userRepository.deleteByUsername(username);
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void subscribe(User user, User publisher) {
        user.getPublishers().add(publisher);
        publisher.getSubscribers().add(user);
        userRepository.save(user);
        userRepository.save(publisher);
    }

    public void removeNotification(User user, Notification notification) {
        user.getNotifications().remove(notification);
        userRepository.save(user);
    }

    public void becomeFriend(User user, User from) {
        user.getFriends().add(from);
        userRepository.save(user);
        from.getFriends().add(user);
        userRepository.save(from);
    }

    public void unSubscribe(User user, User publisher) {
        user.getPublishers().remove(publisher);
        publisher.getSubscribers().remove(user);
        userRepository.save(user);
        userRepository.save(publisher);
    }

    public void deleteFriend(User user, User friend) {
        user.getFriends().remove(friend);
        user.getPublishers().remove(friend);
        userRepository.save(user);
        friend.getFriends().remove(user);
        friend.getSubscribers().remove(user);
        userRepository.save(friend);
    }

    public void addChatToUser(User user, Chat chat) {
        user.getChats().add(chat);
        userRepository.save(user);
    }
}
