package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.dto.classement.ClassementDtoPageAccueil;
import com.dataFoot.ProjetData.mapper.ClassementMapper;
import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.model.League;
import com.dataFoot.ProjetData.model.Match;
import com.dataFoot.ProjetData.repository.ClassementRepository;
import com.dataFoot.ProjetData.repository.LeagueRepository;
import com.dataFoot.ProjetData.repository.MatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassementService {

    private final LeagueRepository leagueRepository;
    private final MatchRepository matchRepository;
    private final ClassementRepository classementRepository;

    public ClassementService(LeagueRepository leagueRepository, MatchRepository matchRepository, ClassementRepository classementRepository) {
        this.leagueRepository = leagueRepository;
        this.matchRepository = matchRepository;
        this.classementRepository = classementRepository;
    }

        public List<ClassementDto> getClassementByLeague(Long leagueId) {

            //  récupérer la ligue
            League league = leagueRepository.findById(leagueId)
                    .orElseThrow(() -> new RuntimeException("League introuvable"));

            //  récupérer les classements avec club chargé
            List<Classement> classements = classementRepository.findByLeagueIdWithClub(league.getId());

            return classements.stream()
                    .map(ClassementMapper::toDto)
                    .toList();
        }

        public List<ClassementDtoPageAccueil> pageAccueilClassement()
        {
            return  classementRepository.findAll().stream().map(ClassementMapper::toDtoPageAccueil).toList();

        }
    @Transactional
    public void recalculateLeague(League league) {

        List<Classement> classements =
                classementRepository.findByLeagueIdWithClub(league.getId());

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
        List<Match> matches = matchRepository.findByLeagueAndPlayedTrue(league)
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

        classementRepository.saveAll(classementByClub.values());
    }

}
