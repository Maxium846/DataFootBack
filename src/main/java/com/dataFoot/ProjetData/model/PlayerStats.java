package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "player")
@Table(name = "stats")
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int matchesPlayed;
    private Long goals;
    private Long assists;
    private Long yellowCards;
    private Long redCards;
    private Long minutesPlayed;
    @OneToOne
    @JoinColumn(name = "player_id")
    private Player player;
}
