package com.qeat.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class ImageUploader {

    private final Path rootDir;
    private final String urlPrefix;

    public ImageUploader(
            @Value("${file.dir}") String fileDir,
            @Value("${file.url-prefix}") String urlPrefix
    ) {
        this.rootDir = Paths.get(fileDir).toAbsolutePath().normalize();
        this.urlPrefix = urlPrefix;
    }

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 없습니다.");
        }

        try {
            Files.createDirectories(rootDir);
        } catch (Exception e) {
            throw new IllegalStateException("업로드 폴더 생성 실패: " + rootDir, e);
        }

        String original = file.getOriginalFilename();
        String ext = getExt(original);
        String savedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
        Path target = rootDir.resolve(savedName);

        try {
            file.transferTo(target.toFile());
        } catch (Exception e) {
            throw new IllegalStateException("이미지 저장 실패", e);
        }

        return urlPrefix + "/" + savedName;
    }

    private String getExt(String name) {
        if (name == null) {
            return "";
        }
        int idx = name.lastIndexOf('.');
        if (idx < 0 || idx == name.length() - 1) {
            return "";
        }
        return name.substring(idx + 1).toLowerCase();
    }
}
