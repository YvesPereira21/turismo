package io.turismo.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "photo_id")
    private UUID photoId;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_spot_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TouristSpot touristSpot;

    @OneToOne(mappedBy = "photo")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Activity activity;
}
