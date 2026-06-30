package io.turismo.backend.model;

import io.turismo.backend.model.enums.TourGuideType;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tour_guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class TourGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tour_guide_id")
    private UUID tourGuideId;

    @Column(name = "cadastur" , nullable = false, unique = true)
    private String cadastur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TourGuideType type;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Builder.Default
    @ManyToMany(mappedBy = "tourGuides", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<TouristSpot> touristSpots = new HashSet<>();

}
