package io.turismo.backend.dto.photo;

import org.springframework.web.multipart.MultipartFile;

public record PhotoUploadDTO(
        MultipartFile photo,
        String altText
) {}
