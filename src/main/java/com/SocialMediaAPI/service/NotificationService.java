package com.SocialMediaAPI.service;

import com.SocialMediaAPI.exception.NotificationNotFoundEcxeption;
import com.SocialMediaAPI.model.Notification;
import com.SocialMediaAPI.model.NotificationType;
import com.SocialMediaAPI.model.Sender;
import com.SocialMediaAPI.model.User;
import com.SocialMediaAPI.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public boolean isInviteExist(User user, User potentialFriend) {
        NotificationType type = NotificationType.FRIEND_INVITE;
        return notificationRepository.isInviteExist(user, potentialFriend, type).isPresent();
    }

    public Notification findNotificationById(long id) {
        return notificationRepository.findById(id).orElseThrow(() -> new NotificationNotFoundEcxeption("Notification with id: "+ id + " not found!"));
    }

    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }

    public List<Notification> findAllNotificationByUser(User user) {
        return notificationRepository.findAllByTo(user);
    }
}
