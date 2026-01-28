package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
    public class MatchService {

        private final MatchRepositoryInterface matchRepo;
        private  final ClassementService classementService;
        private final LeagueRepositoryInterface leagueRepositoryInterface;


        public MatchService(MatchRepositoryInterface matchRepo, ClassementService classementService, LeagueRepositoryInterface leagueRepositoryInterface) {
            this.matchRepo = matchRepo;
            this.classementService = classementService;
            this.leagueRepositoryInterface = leagueRepositoryInterface;
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


    @Transactional
    public void generateCalendar(Long leagueId) {

        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Ligue introuvable"));

        List<Club> clubs = league.getClubs();

        if (clubs.size() < 2) {
            throw new RuntimeException("Pas assez de clubs");
        }

        int n = clubs.size();
        boolean isOdd = n % 2 != 0;

        if (isOdd) {
            clubs.add(null); // bye week
            n++;
        }

        int totalRounds = (n - 1) * 2;
        int matchesPerRound = n / 2;

        List<Club> rotation = new ArrayList<>(clubs);

        List<Match> matches = new ArrayList<>();

        for (int round = 1; round <= totalRounds; round++) {

            for (int i = 0; i < matchesPerRound; i++) {

                Club home = rotation.get(i);
                Club away = rotation.get(n - 1 - i);

                if (home == null || away == null) continue;

                // phase retour
                if (round > (totalRounds / 2)) {
                    Club tmp = home;
                    home = away;
                    away = tmp;
                }

                Match match = new Match();
                match.setLeague(league);
                match.setJournee(round);
                match.setHomeClub(home);
                match.setAwayClub(away);
                match.setPlayed(false);

                matches.add(match);
            }

            // rotation
            rotation.add(1, rotation.remove(rotation.size() - 1));
        }

        matchRepo.saveAll(matches);
    }

    }


