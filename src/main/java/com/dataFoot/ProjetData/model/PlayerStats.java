package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "player")
@Table(name = "stats")
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int matchesPlayed;
    private int goals;
    private int assists;
    private int yellowCards;
    private int redCards;
    private int minutesPlayed;
    private int ownGoals;
    private int penaltyMissed;
    private int penalty;
    @OneToOne
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private Player player;
}
