package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.match.FplFixture;
import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CalendarService {


    private final LeagueRepositoryInterface leagueRepositoryInterface;
    private final ClassementRepositoryInterface classementRepositoryInterface;

    private final ClubRepositoryInterface clubRepositoryInterface;

    private final MatchRepositoryInterface matchRepositoryInterface;

    private final FplService fplService;
     private final MatchLineUpRepository matchLineUpRepository;

    public CalendarService(LeagueRepositoryInterface leagueRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface, ClubRepositoryInterface clubRepositoryInterface, MatchRepositoryInterface matchRepositoryInterface, FplService fplService, MatchLineUpRepository matchLineUpRepository) {

        this.leagueRepositoryInterface = leagueRepositoryInterface;
        this.classementRepositoryInterface = classementRepositoryInterface;
        this.clubRepositoryInterface = clubRepositoryInterface;
        this.matchRepositoryInterface = matchRepositoryInterface;
        this.fplService = fplService;
        this.matchLineUpRepository = matchLineUpRepository;
    }

    // Si une des méthode ne marche pas , cela plante et n'enregistre rien en base
    @Transactional
    public void generateCalendar(Long leagueId) {
        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Ligue introuvable"));

        // 🔹 Nettoyage
        matchRepositoryInterface.deleteByLeagueId(leagueId);
        classementRepositoryInterface.deleteByLeagueId(leagueId);

        // 🔹 Récupération des clubs
        List<Club> clubs = clubRepositoryInterface.findByLeagueId(leagueId);
        int n = clubs.size();
        if (n < 2) throw new RuntimeException("Pas assez de clubs");

        // 🔹 Création du classement initial
        List<Classement> classements = new ArrayList<>();


        //pour chaque objet club contenu dans la liste clubs, creer un nouvel objet classement
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


        // Si nombre impair, ajouter un club fictif
        boolean hasDummy = false;
        if (clubs.size() % 2 != 0) {
            Club dummy = new Club();
            dummy.setId(-1L);
            dummy.setName("BYE");
            clubs.add(dummy);
            hasDummy = true;
        }


        int totalRounds = (n - 1) * 2; // aller-retour
        int halfRounds = n - 1;

        // 🔹 Initialisation streak domicile/extérieur
        int[][] streaks = new int[n][2]; // [clubIndex][0] = home streak, [1] = away streak

        // 🔹 Rotation pour le round-robin
        List<Club> rotation = new ArrayList<>(clubs);

        List<Match> matches = new ArrayList<>();

        Random random = new Random();

        for (int round = 1; round <= totalRounds; round++) {
            List<Match> roundMatches = new ArrayList<>();

            // 🔹 Shuffle léger pour l'aléatoire
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < n; i++) indices.add(i);
            Collections.shuffle(indices, random);

            for (int i = 0; i < n / 2; i++) {
                int homeIdx = indices.get(i);
                int awayIdx = indices.get(n - 1 - i);

                Club home = rotation.get(homeIdx);
                Club away = rotation.get(awayIdx);

                if (home.getId() == -1L || away.getId() == -1L) continue;

                // 🔹 Équilibrage streak max 2
                if (streaks[homeIdx][0] >= 2) {
                    Club tmp = home;
                    home = away;
                    away = tmp;
                    streaks[homeIdx][0] = 1;
                    streaks[homeIdx][1] = 0;
                    streaks[awayIdx][1] = 1;
                    streaks[awayIdx][0] = 0;
                } else if (streaks[awayIdx][1] >= 2) {
                    Club tmp = home;
                    home = away;
                    away = tmp;
                    streaks[homeIdx][0] = 1;
                    streaks[homeIdx][1] = 0;
                    streaks[awayIdx][1] = 1;
                    streaks[awayIdx][0] = 0;
                } else {
                    streaks[homeIdx][0]++;
                    streaks[homeIdx][1] = 0;
                    streaks[awayIdx][1]++;
                    streaks[awayIdx][0] = 0;
                }

                // 🔹 Créer le match
                Match m = new Match();
                m.setLeague(league);
                m.setJournee(round);
                m.setHomeClub(home);
                m.setAwayClub(away);
                m.setHomeGoals(0);
                m.setAwayGoals(0);
                m.setPlayed(false);

                roundMatches.add(m);
            }

            matches.addAll(roundMatches);

            // 🔹 Rotation circulaire pour la prochaine journée
            List<Club> newRotation = new ArrayList<>();
            newRotation.add(rotation.get(0)); // pivot fixe
            newRotation.add(rotation.get(n - 1));
            for (int j = 1; j < n - 1; j++) {
                newRotation.add(rotation.get(j));
            }
            rotation = newRotation;
        }

        // 🔹 Sauvegarde finale
        matchRepositoryInterface.saveAll(matches);
    }

    public String generateFromPL(Long leagueId) {
        // 🔹 Récupérer la ligue
        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        // 🔹 Supprimer tous les anciens matchs de cette ligue
        matchLineUpRepository.deleteByLeagueId(leagueId);
        matchRepositoryInterface.deleteByLeagueId(leagueId);
        classementRepositoryInterface.deleteByLeagueId(leagueId);


        List<Club> clubs = clubRepositoryInterface.findByLeagueId(leagueId);
        int n = clubs.size();
        if (n < 2) throw new RuntimeException("Pas assez de clubs");

        // 🔹 Création du classement initial
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

        // 🔹 Récupérer les fixtures depuis FPL
        List<FplFixture> fixtures = fplService.getFixtures();

        int createdCount = 0;

        for (FplFixture fixture : fixtures) {

            // Ignorer les matchs sans journée assignée
            if (fixture.getEvent() == null) continue;

            // Récupérer les clubs domicile et extérieur pour chaque match
            Club home = clubRepositoryInterface.findByFplId(fixture.getTeamHomeId())
                    .orElseThrow(() -> new RuntimeException("Home club not found: " + fixture.getTeamHomeId()));
            Club away = clubRepositoryInterface.findByFplId(fixture.getTeamAwayId())
                    .orElseThrow(() -> new RuntimeException("Away club not found: " + fixture.getTeamAwayId()));

            // 🔹 Créer le match
            Match match = new Match();
            match.setLeague(league);
            match.setHomeClub(home);
            match.setAwayClub(away);
            match.setJournee(fixture.getEvent());
            match.setMatchDate(fixture.getKickoffTime().toLocalDate());
            match.setAwayGoals(fixture.getTeamAwayScore());
            match.setHomeGoals(fixture.getTeamHomeScore());

            // Tous les matchs sont initialisés comme non joués

            // Sauvegarde
            matchRepositoryInterface.save(match);
            createdCount++;
        }

        return createdCount + " matchs importés avec succès !";
    }

}


