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
        private final LeagueRepositoryInterface leagueRepositoryInterface;

        private final ClubRepositoryInterface clubRepositoryInterface;

private final ClassementRepositoryInterface classementRepositoryInterface;
        public MatchService(MatchRepositoryInterface matchRepo, ClassementService classementService, LeagueRepositoryInterface leagueRepositoryInterface, ClubRepositoryInterface clubRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface) {
            this.matchRepo = matchRepo;
            this.classementService = classementService;
            this.leagueRepositoryInterface = leagueRepositoryInterface;
            this.clubRepositoryInterface = clubRepositoryInterface;
            this.classementRepositoryInterface = classementRepositoryInterface;
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
        // Récupérer la ligue
        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Ligue introuvable"));

        // 🔹 Récupérer tous les clubs de la ligue
        List<Club> clubsFromDb = clubRepositoryInterface.findByLeagueId(leagueId);

        if (clubsFromDb.size() < 2) {
            throw new RuntimeException("Pas assez de clubs pour générer un calendrier");
        }

        // ⚡ Créer le calendrier
        List<Club> clubs = new ArrayList<>(clubsFromDb);

        boolean isOdd = clubs.size() % 2 != 0;
        if (isOdd) clubs.add(null); // bye week si impair
        int n = clubs.size();

        int totalRounds = (n - 1) * 2;
        int matchesPerRound = n / 2;

        List<Club> rotation = new ArrayList<>(clubs);
        List<Match> matches = new ArrayList<>();

        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                Club home = rotation.get(i);
                Club away = rotation.get(n - 1 - i);

                if (home == null || away == null) continue; // ignorer bye week

                // Phase retour
                if (round > totalRounds / 2) {
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

            // Rotation des clubs pour la prochaine journée
            rotation.add(1, rotation.remove(rotation.size() - 1));
        }

        // Sauvegarder tous les matchs
        matchRepo.saveAll(matches);

        // 🔹 Générer le classement
        for (Club c : clubsFromDb) { // ignorer le null ajouté pour bye week
            // Recharger le club attaché à la session
            Club club = clubRepositoryInterface.getReferenceById(c.getId());

            Classement classement = new Classement();
            classement.setClub(club);    // ✅ club attaché
            classement.setLeague(league);
            classement.setPoints(0);
            classement.setPlayed(0);
            classement.setWins(0);
            classement.setDraws(0);
            classement.setLosses(0);
            classement.setGoalsFor(0);
            classement.setGoalsAgainst(0);

            classementRepositoryInterface.save(classement);
        }
    }





}


