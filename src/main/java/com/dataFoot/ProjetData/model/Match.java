package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "matches")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate matchDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "home_club_id", nullable = false)
    private Club homeClub;

    @ManyToOne(optional = false)
    @JoinColumn(name = "away_club_id", nullable = false)
    private Club awayClub;


    private Integer homeGoals;
    private Integer awayGoals;

    private boolean played;

    @ManyToOne(optional = false)
    private League league;

    @Column(nullable = false)
    private Integer journee;
}
