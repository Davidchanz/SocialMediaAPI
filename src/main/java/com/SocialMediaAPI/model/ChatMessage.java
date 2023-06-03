package com.SocialMediaAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @NotNull
    @Column(length = 1000)
    private String message;

    @CreationTimestamp
    private Instant created;

    @NotNull
    @ManyToOne
    private User author;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
}
