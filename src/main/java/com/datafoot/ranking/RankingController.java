package com.datafoot.ranking;

import com.datafoot.ranking.rankingdto.RankingDto;
import com.datafoot.ranking.rankingdto.RankinkDtoAccueil;
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

    //ok
    @GetMapping("/{leagueId}/classement")
    public List<RankingDto> getClassement(@PathVariable Long leagueId){
        return rankingService.getClassementByLeague(leagueId);
    }

    @GetMapping("/classementAccueil")
    public List<RankinkDtoAccueil>pageAccueilClassemen(){

        return rankingService.pageAccueilClassement();
    }

}



