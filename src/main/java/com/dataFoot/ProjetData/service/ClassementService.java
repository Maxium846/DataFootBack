package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.mapper.ClassementMapper;
import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.ClassementRepositoryInterface;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassementService {

    private final LeagueRepositoryInterface leagueRepositoryInterface;
    private final MatchRepositoryInterface matchRepositoryInterface;
    private final ClassementRepositoryInterface classementRepositoryInterface;

    public ClassementService(LeagueRepositoryInterface leagueRepositoryInterface, MatchRepositoryInterface matchRepositoryInterface, ClassementRepositoryInterface classementRepositoryInterface) {
        this.leagueRepositoryInterface = leagueRepositoryInterface;
        this.matchRepositoryInterface = matchRepositoryInterface;
        this.classementRepositoryInterface = classementRepositoryInterface;
    }

        public List<ClassementDto> getClassementByLeague(Long leagueId) {

            // ✅ récupérer la ligue
            League league = leagueRepositoryInterface.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("League introuvable"));

            // ✅ récupérer les classements avec club chargé
            List<Classement> classements = classementRepositoryInterface.findByLeagueIdWithClub(league.getId());

            // ✅ mapper et trier
            return classements.stream()
                    .map(ClassementMapper::toDto)
                    .sorted(
                            Comparator.comparingInt(ClassementDto::getPoints).reversed()
                                    .thenComparing(Comparator.comparingInt(ClassementDto::getGoalDifference).reversed())
                    )
                    .toList();
        }



    private Classement getOrCreateClassement(League league, Club club) {
        return classementRepositoryInterface
                .findByLeagueAndClub(league, club)
                .orElseGet(() -> {
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
                    return classementRepositoryInterface.save(c);
                });
    }



    @Transactional
    public void recalculateLeague(League league) {

        List<Classement> classements =
                classementRepositoryInterface.findByLeagueIdWithClub(league.getId());

        if (classements.isEmpty()) {
            throw new IllegalStateException("Classement non initialisé pour la ligue");
        }

        Map<Long, Classement> classementByClub = classements.stream()
                .collect(Collectors.toMap(c -> c.getClub().getId(), c -> c));

        // 🔹 Reset
        classementByClub.values().forEach(c -> {
            c.setPoints(0);
            c.setPlayed(0);
            c.setWins(0);
            c.setDraws(0);
            c.setLosses(0);
            c.setGoalsFor(0);
            c.setGoalsAgainst(0);
            c.setGoalDifference(0);
        });

        // 🔹 Seuls les matchs joués avec scores valides
        List<Match> matches = matchRepositoryInterface.findByLeagueAndPlayedTrue(league)
                .stream()
                .filter(m -> m.getHomeGoals() != null && m.getAwayGoals() != null)
                .toList();

        for (Match m : matches) {
            Classement home = classementByClub.get(m.getHomeClub().getId());
            Classement away = classementByClub.get(m.getAwayClub().getId());

            home.setPlayed(home.getPlayed() + 1);
            away.setPlayed(away.getPlayed() + 1);

            home.setGoalsFor(home.getGoalsFor() + m.getHomeGoals());
            home.setGoalsAgainst(home.getGoalsAgainst() + m.getAwayGoals());

            away.setGoalsFor(away.getGoalsFor() + m.getAwayGoals());
            away.setGoalsAgainst(away.getGoalsAgainst() + m.getHomeGoals());

            if (m.getHomeGoals() > m.getAwayGoals()) {
                home.setWins(home.getWins() + 1);
                home.setPoints(home.getPoints() + 3);
                away.setLosses(away.getLosses() + 1);
            } else if (m.getHomeGoals() < m.getAwayGoals()) {
                away.setWins(away.getWins() + 3);
                away.setPoints(away.getPoints() + 3);
                home.setLosses(home.getLosses() + 1);
            } else {
                home.setDraws(home.getDraws() + 1);
                away.setDraws(away.getDraws() + 1);
                home.setPoints(home.getPoints() + 1);
                away.setPoints(away.getPoints() + 1);
            }
        }

        classementByClub.values().forEach(c ->
                c.setGoalDifference(c.getGoalsFor() - c.getGoalsAgainst())
        );

        classementRepositoryInterface.saveAll(classementByClub.values());
    }



    @Transactional
    public List<ClassementDto> updateMatchScoreAndRecalculate(Long matchId, int homeGoals, int awayGoals) {
        Match match = matchRepositoryInterface.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match introuvable"));

        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);
        match.setPlayed(true);
        matchRepositoryInterface.save(match);

        // Recalculer le classement pour la ligue du match
        recalculateLeague(match.getLeague());

        // ⚡ Renvoyer directement le classement mis à jour et trié
        return getClassementByLeague(match.getLeague().getId());
    }

}
