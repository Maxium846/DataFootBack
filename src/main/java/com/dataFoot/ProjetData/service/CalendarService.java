package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.fpl.MatchDtoFpl;
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


    private final LeagueRepository leagueRepository;
    private final ClassementRepository classementRepository;

    private final ClubRepository clubRepository;

    private final MatchRepository matchRepository;

    private final FplService fplService;
     private final MatchLineUpRepository matchLineUpRepository;
     private final ClassementService classementService;

    public CalendarService(LeagueRepository leagueRepository, ClassementRepository classementRepository, ClubRepository clubRepository, MatchRepository matchRepository, FplService fplService, MatchLineUpRepository matchLineUpRepository, ClassementService classementService) {

        this.leagueRepository = leagueRepository;
        this.classementRepository = classementRepository;
        this.clubRepository = clubRepository;
        this.matchRepository = matchRepository;
        this.fplService = fplService;
        this.matchLineUpRepository = matchLineUpRepository;
        this.classementService = classementService;
    }


@Transactional
    public String generateCalendarFromPl(Long leagueId) {
        // 🔹 Récupérer la ligue
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        // 🔹 Supprimer tous les anciens matchs et classements
        matchLineUpRepository.deleteByLeagueId(leagueId);
        matchRepository.deleteByLeagueId(leagueId);
        classementRepository.deleteByLeagueId(leagueId);

        // 🔹 Récupérer les clubs de la ligue
        List<Club> clubs = clubRepository.findByLeagueId(leagueId);
        if (clubs.size() < 2) throw new RuntimeException("Pas assez de clubs");

        // 🔹 Créer le classement initial
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
        classementRepository.saveAll(classements);

        // 🔹 Récupérer les fixtures depuis FPL
        List<MatchDtoFpl> fixtures = fplService.getFixtures();

        int createdCount = 0;

        for (MatchDtoFpl fixture : fixtures) {

            // Ignorer les matchs sans journée assignée
            if (fixture.getEvent() == null) continue;

            // 🔹 Récupérer les clubs via fplId
            Club home = clubRepository.findByFplId(fixture.getTeamHomeId())
                    .orElseThrow(() -> new RuntimeException("Home club not found: " + fixture.getTeamHomeId()));
            Club away = clubRepository.findByFplId(fixture.getTeamAwayId())
                    .orElseThrow(() -> new RuntimeException("Away club not found: " + fixture.getTeamAwayId()));

            // 🔹 Créer le match
            Match match = new Match();
            match.setLeague(league);
            match.setHomeClub(home);             // relation JPA classique
            match.setAwayClub(away);             // relation JPA classique
            match.setJournee(fixture.getEvent());
            match.setMatchDate(fixture.getKickoffTime().toLocalDate());
            match.setHomeGoals(fixture.getTeamHomeScore());
            match.setAwayGoals(fixture.getTeamAwayScore());
            match.setPlayed(Boolean.TRUE.equals(fixture.getFinished()));

            // 🔹 Stocker les IDs FPL pour cross-check
            match.setFplId(fixture.getId());
            match.setHomeClubFplId(home.getFplId());
            match.setAwayClubFplId(away.getFplId());

            // 🔹 Sauvegarde
            matchRepository.save(match);
            createdCount++;
        }

        // 🔹 Recalculer le classement après insertion de tous les matchs
        classementService.recalculateLeague(league);

        return createdCount + " matchs importés et classement recalculé avec succès !";
    }


}


