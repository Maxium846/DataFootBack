package com.dataFoot.ranking;

import com.dataFoot.ranking.rankingdto.RankingDto;
import com.dataFoot.ranking.rankingdto.RankinkDtoAccueil;
import com.dataFoot.league.League;
import com.dataFoot.match.Match;
import com.dataFoot.league.LeagueRepository;
import com.dataFoot.match.MatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final LeagueRepository leagueRepository;
    private final MatchRepository matchRepository;
    private final RankingRepository rankingRepository;

    public RankingService(LeagueRepository leagueRepository, MatchRepository matchRepository, RankingRepository rankingRepository) {
        this.leagueRepository = leagueRepository;
        this.matchRepository = matchRepository;
        this.rankingRepository = rankingRepository;
    }

        public List<RankingDto> getClassementByLeague(Long leagueId) {

            //  récupérer la ligue
            League league = leagueRepository.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("League introuvable"));

            //  récupérer les classements avec club chargé
            List<Ranking> rankings = rankingRepository.findByLeagueIdWithClub(league.getId());

            return rankings.stream()
                    .map(RankingMapper::toDto)
                    .toList();
        }

        public List<RankinkDtoAccueil> pageAccueilClassement()
        {
            return  rankingRepository.findAll().stream().map(RankingMapper::toDtoPageAccueil).toList();

        }
    @Transactional
    public void recalculateLeague(League league) {

        List<Ranking> rankings =
                rankingRepository.findByLeagueIdWithClub(league.getId());

        if (rankings.isEmpty()) {
            throw new IllegalStateException("Classement non initialisé pour la ligue");
        }

        Map<Long, Ranking> classementByClub = rankings.stream()
                .collect(Collectors.toMap(c -> c.getTeam().getId(), c -> c));

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

        List<Match> matches = matchRepository.findByLeagueAndPlayedTrue(league)
                .stream()
                .filter(m -> m.getHomeGoals() != null && m.getAwayGoals() != null)
                .toList();

        for (Match m : matches) {
            Ranking home = classementByClub.get(m.getHomeTeam().getId());
            Ranking away = classementByClub.get(m.getAwayTeam().getId());

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

        classementByClub.values().forEach(c ->
                c.setGoalDifference(c.getGoalsFor() - c.getGoalsAgainst())
        );

        rankingRepository.saveAll(classementByClub.values());
    }

}
