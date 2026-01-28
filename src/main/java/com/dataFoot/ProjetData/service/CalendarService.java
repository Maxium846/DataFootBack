package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.ClassementRepositoryInterface;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CalendarService {

    private final ClubRepositoryInterface clubRepository;
    private final MatchRepositoryInterface matchRepository;

    private final LeagueRepositoryInterface leagueRepositoryInterface;
    private final ClassementRepositoryInterface classementRepositoryInterface;
    public CalendarService(ClubRepositoryInterface clubRepository,
                           MatchRepositoryInterface matchRepository, LeagueRepositoryInterface leagueRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface) {
        this.clubRepository = clubRepository;
        this.matchRepository = matchRepository;
        this.leagueRepositoryInterface = leagueRepositoryInterface;
        this.classementRepositoryInterface = classementRepositoryInterface;
    }

    public void generateCalendar(Long leagueId) {

        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League introuvable"));

        // Récupère tous les clubs de la ligue
        List<Club> clubs = new ArrayList<>(clubRepository.findByLeagueId(leagueId));

        if (clubs.size() % 2 != 0) {
            // Ajouter un "ghost" si nombre impair
            clubs.add(null);
        }

        int numClubs = clubs.size();
        int numJournees = numClubs - 1;
        int halfSize = numClubs / 2;

        List<Match> allMatches = new ArrayList<>();

        List<Club> rotation = new ArrayList<>(clubs);
        Club fixed = rotation.get(0); // Club fixe pour le round-robin

        for (int journee = 1; journee <= numJournees; journee++) {
            List<Match> matchesThisRound = new ArrayList<>();

            for (int i = 0; i < halfSize; i++) {
                Club home, away;

                if (i == 0) {
                    home = fixed;
                    away = rotation.get(rotation.size() - 1);
                } else {
                    home = rotation.get(i);
                    away = rotation.get(rotation.size() - 1 - i);
                }

                if (home != null && away != null) {
                    // Alterner domicile/extérieur
                    if (journee % 2 == 0) {
                        Match match = new Match();
                        match.setLeague(league);
                        match.setHomeClub(home);
                        match.setAwayClub(away);
                        match.setJournee(journee);
                        match.setHomeGoals(0);
                        match.setAwayGoals(0);
                        matchesThisRound.add(match);
                    } else {
                        Match match = new Match();
                        match.setLeague(league);
                        match.setHomeClub(away);
                        match.setAwayClub(home);
                        match.setJournee(journee);
                        match.setHomeGoals(0);
                        match.setAwayGoals(0);
                        matchesThisRound.add(match);
                    }
                }
            }

            allMatches.addAll(matchesThisRound);

            // Rotation pour la prochaine journée (circle method)
            List<Club> newRotation = new ArrayList<>();
            newRotation.add(rotation.get(0)); // fixed stays
            newRotation.add(rotation.get(rotation.size() - 1)); // last moves to 2nd position
            newRotation.addAll(rotation.subList(1, rotation.size() - 1));
            rotation = newRotation;
        }

        // Supprimer anciens matchs de la ligue

        // Sauvegarder tous les matchs
        matchRepository.saveAll(allMatches);

        List<Classement> initialClassement = clubs.stream()
                .map(club -> {
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
                    return c;
                })
                .toList();

        classementRepositoryInterface.saveAll(initialClassement);
    }
    }

