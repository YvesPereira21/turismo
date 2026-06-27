package io.turismo.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tourist_spots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tourist_spot_id")
    private UUID touristSpotId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(name = "opens_at")
    private LocalTime opensAt;

    @Column(name = "closes_at")
    private LocalTime closesAt;

    @Column(length = 255)
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_manager_id", nullable = false)
    private SpotManager spotManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @OneToMany(mappedBy = "touristSpot", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Photo> photos;

    @Builder.Default
    @OneToMany(mappedBy = "touristSpot", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Activity> activities = new HashSet<>();

    @OneToMany(mappedBy = "touristSpot", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Warn> warns;

    @ManyToMany
    @JoinTable(
        name = "tourist_spot_tags",
        joinColumns = @JoinColumn(name = "tourist_spot_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @ManyToMany
    @JoinTable(
        name = "tourist_spot_tour_guides",
        joinColumns = @JoinColumn(name = "tourist_spot_id"),
        inverseJoinColumns = @JoinColumn(name = "tour_guide_id")
    )
    private List<TourGuide> tourGuides;
}
