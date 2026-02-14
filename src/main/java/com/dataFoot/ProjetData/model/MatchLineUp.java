package com.dataFoot.ProjetData.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "match_lineup")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchLineUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Match match;
    @ManyToOne(optional = false)
    private Player player;
    @ManyToOne(optional = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private  League league;
    private String position;
    private Boolean starter =true;
}
