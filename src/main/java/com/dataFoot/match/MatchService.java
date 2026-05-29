package com.dataFoot.match;

import com.dataFoot.ranking.RankingRepository;
import com.dataFoot.ranking.RankingService;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.league.LeagueRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
    public class MatchService {

        private final MatchRepository matchRepo;
        private  final RankingService rankingService;

        public MatchService(MatchRepository matchRepo, RankingService rankingService, LeagueRepository leagueRepository, TeamRepository teamRepository, RankingRepository rankingRepository) {
            this.matchRepo = matchRepo;
            this.rankingService = rankingService;
        }

    @Transactional
    public void updateScore(Long matchId, int homeGoals, int awayGoals) {

        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match introuvable"));

        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);
        match.setPlayed(true);

        matchRepo.save(match);

        rankingService.recalculateLeague(match.getLeague());
    }

}


