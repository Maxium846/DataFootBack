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
    public CalendarService(ClubRepositoryInterface clubRepository,
                           MatchRepositoryInterface matchRepository, LeagueRepositoryInterface leagueRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface, ClubRepositoryInterface clubRepositoryInterface) {
        this.clubRepository = clubRepository;
        this.matchRepository = matchRepository;
        this.leagueRepositoryInterface = leagueRepositoryInterface;
        this.classementRepositoryInterface = classementRepositoryInterface;
        this.clubRepositoryInterface = clubRepositoryInterface;
    }

    @Transactional
    public void generateCalendar(Long leagueId) {

        // 🔹 Ligue
        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("Ligue introuvable"));

        // 🔹 Clubs réels
        List<Club> realClubs = clubRepositoryInterface.findByLeagueId(leagueId);
        if (realClubs.size() < 2) {
            throw new RuntimeException("Pas assez de clubs pour générer un calendrier");
        }

        // 🔹 Nettoyage
        matchRepository.deleteByLeagueId(leagueId);
        classementRepositoryInterface.deleteByLeagueId(leagueId);

        // =====================================================
        // ✅ 1️⃣ CRÉER LE CLASSEMENT D’ABORD
        // =====================================================
        List<Classement> classements = realClubs.stream()
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

        classementRepositoryInterface.saveAll(classements);

        // =====================================================
        // ✅ 2️⃣ ENSUITE LE CALENDRIER
        // =====================================================
        List<Club> calendarClubs = new ArrayList<>(realClubs);
        if (calendarClubs.size() % 2 != 0) {
            calendarClubs.add(null); // bye week
        }

        int n = calendarClubs.size();
        int totalRounds = (n - 1) * 2;
        int matchesPerRound = n / 2;

        List<Match> allMatches = new ArrayList<>();
        List<Club> rotation = new ArrayList<>(calendarClubs);

        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                Club home = rotation.get(i);
                Club away = rotation.get(n - 1 - i);

                if (home == null || away == null) continue;

                if (round > totalRounds / 2) {
                    Club tmp = home;
                    home = away;
                    away = tmp;
                }

                Match match = new Match();
                match.setLeague(league);
                match.setHomeClub(home);
                match.setAwayClub(away);
                match.setJournee(round);
                match.setHomeGoals(0);
                match.setAwayGoals(0);
                match.setPlayed(false);

                allMatches.add(match);
            }

            // rotation
            List<Club> newRotation = new ArrayList<>();
            newRotation.add(rotation.get(0));
            newRotation.add(rotation.get(rotation.size() - 1));
            newRotation.addAll(rotation.subList(1, rotation.size() - 1));
            rotation = newRotation;
        }

        matchRepository.saveAll(allMatches);
    }


}


