package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classement",
        uniqueConstraints = @UniqueConstraint(columnNames = {"league_id", "club_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Classement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    // Plusieurs lignes de classement pourle meme club
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    //plusieurs lignes de Classement pour la meme ligue
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    private int points;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
}
