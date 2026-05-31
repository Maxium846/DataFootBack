package com.datafoot.ranking;

import com.datafoot.ranking.rankingdto.RankingDto;
import com.datafoot.ranking.rankingdto.RankingDtoAccueil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/{leagueId}/ranking")
    public List<RankingDto> getRanking(@PathVariable Long leagueId){
        return rankingService.getClassementByLeague(leagueId);
    }

    @GetMapping("/classementAccueil")
    public List<RankingDtoAccueil>pageAccueilRanking(){

        return rankingService.pageAccueilClassement();
    }

}



