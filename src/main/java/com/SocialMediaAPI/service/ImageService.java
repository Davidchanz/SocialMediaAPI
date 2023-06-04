package com.SocialMediaAPI.service;

import com.SocialMediaAPI.exception.ImageNotFoundException;
import com.SocialMediaAPI.exception.IncompatibleTypeError;
import com.SocialMediaAPI.model.Image;
import com.SocialMediaAPI.repository.ImageRepository;
import com.SocialMediaAPI.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    private final Set<String> imageContentType = Set.of(
            "image/gif",
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public Image uploadImage(MultipartFile file) throws NullPointerException, IOException {
        Image image = new Image();
        if (!imageContentType.contains(file.getContentType()))
            throw new IncompatibleTypeError("File: '" + file.getOriginalFilename() + "' has Incompatible type: " + file.getContentType() + ". You can load only images PNG, WEBP, JPEG, GIF!");
        image.setType(file.getContentType());
        image.setImageData(ImageUtils.compressImage(file.getBytes()));
        image.setName(file.getOriginalFilename());
        return imageRepository.save(image);
    }

    public byte[] downloadImage(Long id) throws ImageNotFoundException {
        Optional<Image> image = imageRepository.findById(id);
        return ImageUtils.decompressImage(image.orElseThrow(() -> new ImageNotFoundException("Image with id: " + id + " not found.")).getImageData());
    }

    public void deleteImage(Image image) {
        imageRepository.deleteById(image.getId());
    }

    public Image findById(Long id){
        return imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Image with id: " + id + " not found!"));
    }
}
