package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.ClassementRepositoryInterface;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
    public class MatchService {

        private final MatchRepositoryInterface matchRepo;
        private  final ClassementService classementService;

        public MatchService(MatchRepositoryInterface matchRepo, ClassementService classementService, LeagueRepositoryInterface leagueRepositoryInterface, ClubRepositoryInterface clubRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface) {
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


