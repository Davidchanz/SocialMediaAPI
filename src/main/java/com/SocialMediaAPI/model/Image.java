package com.SocialMediaAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    private String type;

    @Column(nullable = false)
    @NotNull
    private byte[] imageData;
}
