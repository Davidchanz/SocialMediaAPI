package com.SocialMediaAPI.service;

import com.SocialMediaAPI.exception.ImageNotFoundException;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.repository.ImageRepository;
import com.SocialMediaAPI.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Image uploadImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setType(file.getContentType());
        image.setImageData(ImageUtils.compressImage(file.getBytes()));
        image.setName(file.getOriginalFilename());
        return imageRepository.save(image);
    }

    public byte[] downloadImage(Long id) throws ImageNotFoundException {
        Optional<Image> image = imageRepository.findById(id);
        return ImageUtils.decompressImage(image.orElseThrow(() -> new ImageNotFoundException("Image with id: " + id + " not found.")).getImageData());
    }

}
