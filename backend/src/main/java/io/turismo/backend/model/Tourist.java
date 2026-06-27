package io.turismo.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tourists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tourist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="tourist_id")
    private UUID touristId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}
