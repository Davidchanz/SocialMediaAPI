package com.SocialMediaAPI.repository;

import com.SocialMediaAPI.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
