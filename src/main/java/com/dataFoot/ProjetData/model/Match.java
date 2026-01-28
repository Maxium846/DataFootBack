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

    @ManyToOne
    private Club homeClub;

    @ManyToOne
    private Club awayClub;

    private Integer homeGoals;
    private Integer awayGoals;

    private boolean played;

    @ManyToOne
    private League league;

    @Column(nullable = false)
    private Integer journee;
}
