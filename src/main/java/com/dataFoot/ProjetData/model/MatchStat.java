package com.dataFoot.ProjetData.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MatchStat {
   private Long id;
    private int shootsOnGoals;
    private int shootOffGoals;
    private int totalShots;
    private int blockedShots;
    private int shotInsideBox;
    private int shotsOutsideBox;
    private int fouls;
    private int cornerKick;
    private int offsides;
    private int ballPossession;
    private int yellowCards;
    private int redCards;
    private int goalkeeperSave;
    private int totalPasses;
    private int passesAccurate;
    private int passesPourcentage;
    private int expectedGoals;
    private int goalsPrevented;
    @ManyToOne
    private Match matchIdapi;
    @ManyToOne
    private Club clubId;




}
