package io.turismo.backend.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.turismo.backend.model.enums.ManagerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "spot_managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotManager {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="spot_manager_id")
    private UUID spotManagerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_type", nullable = false)
    private ManagerType managerType;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "spotManager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TouristSpot> touristSpots;

    @OneToMany(mappedBy = "spotManager", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<SocialMedia> socialsMedia = new HashSet<>();
}
