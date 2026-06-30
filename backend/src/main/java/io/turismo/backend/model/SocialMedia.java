package io.turismo.backend.model;

import java.util.UUID;

import io.turismo.backend.model.enums.SocialMediaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name="socials_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class SocialMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="social_media_id")
    private UUID socialMediaId;

    @Column(name="social_media_link", nullable=false)
    private String socialMediaLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_media_type", nullable=false)
    private SocialMediaType socialMediaType;

    @ManyToOne
    @JoinColumn(name="spot_manager_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SpotManager spotManager;
}
