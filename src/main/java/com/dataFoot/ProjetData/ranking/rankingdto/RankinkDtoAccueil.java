package com.dataFoot.ProjetData.ranking.rankingdto;

import lombok.Data;

@Data
public class RankinkDtoAccueil {

    private Long clubId;
    private String clubName;
    private int played;
    private int points;
    private String leagueName;

}
