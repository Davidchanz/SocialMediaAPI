package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Notification;
import com.SocialMediaAPI.model.NotificationType;
import com.SocialMediaAPI.model.Sender;
import com.SocialMediaAPI.model.User;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.from = :from AND n.to = :to AND n.type = :type")
    Optional<Notification> isInviteExist(@Param("from") Sender from, @Param("to") User to, @Param("type") NotificationType type);

    List<Notification> findAllByTo(User user);
}
