// FileUploadService.java
package com.plateforme_etudiant.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.base-url:/uploads/}")
    private String baseUrl;

    // Types de fichiers autorisés
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
    private static final String[] ALLOWED_VIDEO_TYPES = {"video/mp4", "video/webm", "video/ogg"};
    private static final String[] ALLOWED_DOC_TYPES = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};

    /**
     * Upload d'une image
     */
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file, "images", ALLOWED_IMAGE_TYPES);
    }

    /**
     * Upload d'une vidéo
     */
    public String uploadVideo(MultipartFile file) throws IOException {
        return uploadFile(file, "videos", ALLOWED_VIDEO_TYPES);
    }

    /**
     * Upload d'un PDF/document
     */
    public String uploadDocument(MultipartFile file) throws IOException {
        return uploadFile(file, "documents", ALLOWED_DOC_TYPES);
    }

    /**
     * Upload générique
     */
    public String uploadFile(MultipartFile file, String subDir, String[] allowedTypes) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier vide");
        }

        // Vérifier le type de fichier
        String contentType = file.getContentType();
        boolean isAllowed = false;
        for (String type : allowedTypes) {
            if (type.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("Type de fichier non autorisé: " + contentType);
        }

        // Créer le dossier d'upload
        Path uploadPath = Paths.get(uploadDir, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String newFilename = timestamp + "_" + uniqueId + extension;

        // Sauvegarder le fichier
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retourner l'URL d'accès
        String fileUrl = baseUrl + subDir + "/" + newFilename;
        log.info("Fichier uploadé: {}", fileUrl);

        return fileUrl;
    }

    /**
     * Upload de l'image de couverture d'un cours
     */
    public String uploadCoverImage(MultipartFile file) throws IOException {
        return uploadImage(file);
    }

    /**
     * Upload du contenu vidéo d'une leçon
     */
    public String uploadLessonVideo(MultipartFile file) throws IOException {
        return uploadVideo(file);
    }
    public String uploadContentFile(MultipartFile file) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Path uploadPath = Paths.get(uploadDir, "contents");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/contents/" + filename;
            log.info("Fichier contenu uploadé: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("Erreur lors de l'upload du fichier: {}", e.getMessage());
            throw new RuntimeException("Impossible d'uploader le fichier: " + e.getMessage());
        }
    }

    /**
     * Upload du contenu PDF d'une leçon
     */
    public String uploadLessonPDF(MultipartFile file) throws IOException {
        return uploadDocument(file);
    }

    /**
     * Supprimer un fichier
     */
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && fileUrl.startsWith(baseUrl)) {
                String relativePath = fileUrl.substring(baseUrl.length());
                Path filePath = Paths.get(uploadDir, relativePath);
                return Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.error("Erreur lors de la suppression du fichier: {}", fileUrl, e);
        }
        return false;
    }
}