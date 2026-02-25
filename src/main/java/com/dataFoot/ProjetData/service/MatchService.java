package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.ClassementRepository;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.LeagueRepository;
import com.dataFoot.ProjetData.repository.MatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
    public class MatchService {

        private final MatchRepository matchRepo;
        private  final ClassementService classementService;

        public MatchService(MatchRepository matchRepo, ClassementService classementService, LeagueRepository leagueRepository, ClubRepository clubRepository, ClassementRepository classementRepository) {
            this.matchRepo = matchRepo;
            this.classementService = classementService;
        }

    @Transactional
    public void updateScore(Long matchId, int homeGoals, int awayGoals) {

        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match introuvable"));

        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);
        match.setPlayed(true);

        matchRepo.save(match);

        classementService.recalculateLeague(match.getLeague());
    }

}


