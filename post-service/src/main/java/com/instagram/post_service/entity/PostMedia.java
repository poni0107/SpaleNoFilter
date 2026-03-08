package com.instagram.post_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_media")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileUrl;      // Putanja do fajla u MinIO/S3
    private String contentType;  // npr. "image/jpeg" ili "video/mp4"
    private String fileName;     // Originalno ime fajla
    private Long fileSize;       // Veličina fajla u bajtovima

    @Builder.Default
    private boolean isActive = true; // Soft delete flag

    @CreationTimestamp
    private LocalDateTime createdAt; // Kada je fajl dodat

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post; // Veza ka postu kojem fajl pripada
}
