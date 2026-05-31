package com.datafoot.ranking.rankingdto;

import lombok.Data;

@Data
public class RankingDto {
    private Long teamId;
    private String teamName;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;
    private int goalDifference;
}
