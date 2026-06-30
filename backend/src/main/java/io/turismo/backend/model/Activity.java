package io.turismo.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "activity_id")
    private UUID activityId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "tourist_spot_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TouristSpot touristSpot;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Photo photo;
}
