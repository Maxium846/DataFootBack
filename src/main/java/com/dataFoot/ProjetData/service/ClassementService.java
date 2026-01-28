package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.ClassementRepositoryInterface;
import com.dataFoot.ProjetData.repository.LeagueRepositoryInterface;
import com.dataFoot.ProjetData.repository.MatchRepositoryInterface;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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

        League league = leagueRepositoryInterface.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League introuvable"));

        return classementRepositoryInterface.findByLeague(league)
                .stream()
                .map(c -> {
                    ClassementDto dto = new ClassementDto();
                    dto.setClubId(c.getClub().getId());
                    dto.setClubName(c.getClub().getName());
                    dto.setPlayed(c.getPlayed());
                    dto.setWins(c.getWins());
                    dto.setDraws(c.getDraws());
                    dto.setLosses(c.getLosses());
                    dto.setGoalsFor(c.getGoalsFor());
                    dto.setGoalsAgainst(c.getGoalsAgainst());
                    dto.setGoalDifference(c.getGoalDifference());
                    dto.setPoints(c.getPoints());
                    return dto;
                })
                .sorted(
                        Comparator.comparingInt(ClassementDto::getPoints).reversed()
                                .thenComparing(Comparator.comparingInt(ClassementDto::getGoalDifference).reversed())
                ).toList();

    }


    @Transactional
    public void recalculateLeague(League league) {

        List<Classement> classements = classementRepositoryInterface.findByLeague(league);
        classements.forEach(c -> {
            c.setPoints(0);
            c.setPlayed(0);
            c.setWins(0);
            c.setDraws(0);
            c.setLosses(0);
            c.setGoalsFor(0);
            c.setGoalsAgainst(0);
            c.setGoalDifference(0);
        });

        List<Match> matches = matchRepositoryInterface.findByLeagueAndPlayedTrue(league);
        for (Match m : matches) {

            Classement home = classementRepositoryInterface
                    .findByLeagueAndClub(league, m.getHomeClub());

            Classement away = classementRepositoryInterface
                    .findByLeagueAndClub(league, m.getAwayClub());

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
                away.setWins(away.getWins() + 1);
                away.setPoints(away.getPoints() + 3);
                home.setLosses(home.getLosses() + 1);
            } else {
                home.setDraws(home.getDraws() + 1);
                away.setDraws(away.getDraws() + 1);
                home.setPoints(home.getPoints() + 1);
                away.setPoints(away.getPoints() + 1);
            }
        }
        classements.forEach(c ->
                c.setGoalDifference(c.getGoalsFor() - c.getGoalsAgainst())
        );

        classementRepositoryInterface.saveAll(classements);
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
