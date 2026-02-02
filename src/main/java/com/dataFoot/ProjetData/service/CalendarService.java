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


@Service
public class CalendarService {

    private final ClubRepositoryInterface clubRepository;
    private final MatchRepositoryInterface matchRepository;

    private final LeagueRepositoryInterface leagueRepositoryInterface;
    private final ClassementRepositoryInterface classementRepositoryInterface;

    private final ClubRepositoryInterface clubRepositoryInterface;

    private final MatchRepositoryInterface matchRepositoryInterface;

    public CalendarService(ClubRepositoryInterface clubRepository,
                           MatchRepositoryInterface matchRepository, LeagueRepositoryInterface leagueRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface, ClubRepositoryInterface clubRepositoryInterface, MatchRepositoryInterface matchRepositoryInterface) {
        this.clubRepository = clubRepository;
        this.matchRepository = matchRepository;
        this.leagueRepositoryInterface = leagueRepositoryInterface;
        this.classementRepositoryInterface = classementRepositoryInterface;
        this.clubRepositoryInterface = clubRepositoryInterface;
        this.matchRepositoryInterface = matchRepositoryInterface;
    }

    @Transactional
    public void generateCalendar(Long leagueId) {
        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Ligue introuvable"));

        // 🔹 Nettoyage
        matchRepositoryInterface.deleteByLeagueId(leagueId);
        classementRepositoryInterface.deleteByLeagueId(leagueId);

        // 🔹 Clubs
        List<Club> clubs = clubRepositoryInterface.findByLeagueId(leagueId);
        int n = clubs.size();
        if (n < 2) throw new RuntimeException("Pas assez de clubs");

        // 🔹 Création classement initial
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

        // 🔹 Ajouter club fictif si impair
        boolean hasDummy = false;
        if (n % 2 != 0) {
            Club dummy = new Club();
            dummy.setId(-1L);
            dummy.setName("BYE");
            clubs.add(dummy);
            n++;
            hasDummy = true;
        }

        // 🔹 Initialisation rotation
        List<Club> rotation = new ArrayList<>(clubs);
        int totalRounds = n - 1; // moitié championnat (aller)
        List<Match> matches = new ArrayList<>();

        // ─────────────── GENERATION ALLER ───────────────
        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < n / 2; i++) {
                Club home = rotation.get(i);
                Club away = rotation.get(n - 1 - i);

                if (home.getId() == -1L || away.getId() == -1L) continue;

                // Alternance simple pour éviter 2 journées domicile consécutives pour pivot
                if (i == 0 && round % 2 == 0) {
                    Club tmp = home;
                    home = away;
                    away = tmp;
                } else if (i > 0 && i % 2 == 1) {
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

            // Rotation circulaire sauf pivot
            List<Club> newRotation = new ArrayList<>();
            newRotation.add(rotation.get(0)); // pivot fixe
            newRotation.add(rotation.get(n - 1));
            for (int j = 1; j < n - 1; j++) {
                newRotation.add(rotation.get(j));
            }
            rotation = newRotation;
        }

        // ─────────────── GENERATION RETOUR ───────────────
        int startRound = totalRounds + 1;
        List<Match> retourMatches = new ArrayList<>();
        for (Match m : matches) {
            Match retour = new Match();
            retour.setLeague(league);
            retour.setJournee(startRound + m.getJournee() - 1); // journéé 20 à 38
            retour.setHomeClub(m.getAwayClub()); // inverser domicile/extérieur
            retour.setAwayClub(m.getHomeClub());
            retour.setHomeGoals(0);
            retour.setAwayGoals(0);
            retour.setPlayed(false);
            retourMatches.add(retour);
        }

        matches.addAll(retourMatches);

        // 🔹 Sauvegarde finale
        matchRepositoryInterface.saveAll(matches);
    }

}


