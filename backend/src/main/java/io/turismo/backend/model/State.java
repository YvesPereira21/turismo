package io.turismo.backend.model;

import io.turismo.backend.model.enums.StateName;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "state_id")
    private UUID stateId;

    private StateName name;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<City> cities;
}
