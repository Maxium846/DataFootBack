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

        System.out.println("🚨 GENERATE CALENDAR APPELÉ 🚨");
        // 🔹 Récupération de la ligue
        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Ligue introuvable"));

        // 🔹 Nettoyage : suppression des anciens matches et classements
        matchRepo.deleteByLeagueId(leagueId);
        classementRepositoryInterface.deleteByLeagueId(leagueId);
        // 🔹 Clubs
        List<Club> clubs = clubRepositoryInterface.findByLeagueId(leagueId);
        if (clubs.size() < 2) {
            throw new RuntimeException("Pas assez de clubs");
        }
        System.out.println("CLUBS DANS LA LIGUE : " + clubs.size());
        clubs.forEach(c ->
                System.out.println("Club id=" + c.getId() + " name=" + c.getName())
        );
        // 🔹 Création du classement pour tous les clubs
        List<Classement> classements = new ArrayList<>();
        for (Club club : clubs) {
            Classement c = new Classement();
            c.setLeague(league);
            c.setClub(club);
            c.setPoints(0);
            c.setPlayed(0);
            c.setWins(0);
            c.setDraws(0);
            c.setLosses(0);
            c.setGoalsFor(0);
            c.setGoalsAgainst(0);
            c.setGoalDifference(0);
            classements.add(c);
        }
        classementRepositoryInterface.saveAll(classements);

        // 🔹 Génération du calendrier aller-retour
        List<Club> calendarClubs = new ArrayList<>(clubs);
        if (calendarClubs.size() % 2 != 0) calendarClubs.add(null); // ajout d'un club fictif si impair

        int n = calendarClubs.size();
        int totalRounds = (n - 1) * 2;
        int matchesPerRound = n / 2;

        List<Club> rotation = new ArrayList<>(calendarClubs);
        List<Match> matches = new ArrayList<>();

        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                Club home = rotation.get(i);
                Club away = rotation.get(n - 1 - i);

                if (home == null || away == null) continue; // ignore le club fictif

                // inversion pour le retour
                if (round > totalRounds / 2) {
                    Club tmp = home;
                    home = away;
                    away = tmp;
                }

                Match m = new Match();
                m.setLeague(league);
                m.setJournee(round);
                m.setHomeClub(home);
                m.setAwayClub(away);
                m.setHomeGoals(0);
                m.setAwayGoals(0);
                m.setPlayed(false);

                matches.add(m);
            }
            // rotation des clubs sauf le premier
            rotation.add(1, rotation.remove(rotation.size() - 1));
        }

        // 🔹 Sauvegarde des matches
        matchRepo.saveAll(matches);
    }





}


