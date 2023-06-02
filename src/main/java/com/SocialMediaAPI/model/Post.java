package com.SocialMediaAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String header;

    @Column
    private String text;

    @Column
    @OneToMany
    private List<Image> images;

    @ManyToOne
    private User user;

    @CreationTimestamp
    private Instant created;
}
