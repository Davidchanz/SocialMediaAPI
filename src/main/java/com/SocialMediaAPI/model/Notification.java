package com.SocialMediaAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @Column
    @NotNull
    private String message;

    @NotNull
    @ManyToOne(targetEntity = AbstractSender.class)
    private Sender from;

    @NotNull
    @ManyToOne
    private User to;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType type;
}
