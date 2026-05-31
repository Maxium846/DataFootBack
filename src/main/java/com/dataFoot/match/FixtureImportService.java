package com.dataFoot.match;

import com.dataFoot.exception.entitexception.ExternalApiException;
import com.dataFoot.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.exception.entitexception.MatchNotFoundException;
import com.dataFoot.exception.entitexception.TeamNotFoundException;
import com.dataFoot.league.League;
import com.dataFoot.league.LeagueRepository;
import com.dataFoot.match.matchdtoapi.ResponseApItemDtoMatch;
import com.dataFoot.match.matchdtoapi.ResponseApiMatch;
import com.dataFoot.ranking.Ranking;
import com.dataFoot.ranking.RankingRepository;
import com.dataFoot.ranking.RankingService;
import com.dataFoot.team.Team;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.team.teamdtoapi.ResponseApiTeamsDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class FixtureImportService {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final RankingRepository rankingRepository;
    private final RankingService rankingService;
    private final RestClient  apiSportClient;


    @Value("${apisports.key}")
    private String apiSportsKey;

    private static final String BASE_URL = "https://v3.football.api-sports.io";

    public FixtureImportService(LeagueRepository leagueRepository,
                                TeamRepository teamRepository,
                                MatchRepository matchRepository,
                                RankingRepository rankingRepository,
                                RankingService rankingService,
                                RestClient apiSportClient) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.rankingRepository = rankingRepository;
        this.rankingService = rankingService;
        this.apiSportClient = apiSportClient;
    }

    @Transactional
    public String generateCalendarFromApiFootball(Long leagueId,int season) {

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new LeagueNotFoundException("La ligue avec l'id  : " + leagueId + " n'existe pas en base"));

        Integer apiFootballLeagueId = league.getApiFootballLeagueId();
        if (apiFootballLeagueId == null) {
            throw new LeagueNotFoundException("la ligue avec l'id" + leagueId + "n'a pas de  correspondant en base ");
        }


        List<Team> clubs = teamRepository.findByLeagueId(leagueId);
        initClassement(league,clubs);

        String path ="/fixtures?league=" + apiFootballLeagueId + "&season=" + season;

        ResponseApiMatch responseApiMatch = callApi(path);
        if (responseApiMatch == null || responseApiMatch.getResponse() == null || responseApiMatch.getResponse().isEmpty()) {

            throw new MatchNotFoundException("Aucun match  n'a été trouvé pour cette ligue et cette saison");
        }
        int created = 0;
        int skipped = 0;
        List<Match> matchSave = new ArrayList<>();
        for (ResponseApItemDtoMatch dto : responseApiMatch.getResponse()){

            Team teamHome = teamRepository.findByApiFootballTeamId(dto.getTeams().getHome().getId()).orElseThrow();
            Team teamAway = teamRepository.findByApiFootballTeamId(dto.getTeams().getAway().getId()).orElseThrow();

            Match match = matchRepository.findByApiFootballFixtureId(dto.getFixture().getId()).orElseGet(Match::new);

            Integer journee = parseRoundToJournee(dto.getLeague().getRound());
            if (journee == null) {
                skipped++;
                continue;
            }
            String isoDate = dto.getFixture().getDate();
            LocalDate matchDate = parseToParisLocalDate(isoDate);
            if (matchDate == null) {
                skipped++;
                continue;
            }
            String statusShort = dto.getFixture().getStatus().getShortStatus();
            boolean played = isPlayed(statusShort) && dto.getGoals().getHome()!= null && dto.getGoals().getAway() != null;
            match.setAwayTeam(teamAway);
            match.setHomeTeam(teamHome);
            match.setAwayGoals(dto.getGoals().getAway());
            match.setHomeGoals(dto.getGoals().getHome());
            match.setApiFootballFixtureId(dto.getFixture().getId());
            match.setLeague(league);
            match.setPlayed(played);
            match.setJournee(journee);
            match.setMatchDate(matchDate);


            matchSave.add(match);

        }

        matchRepository.saveAll(matchSave);
        created++;
        rankingService.recalculateLeague(league);

        return created + " matchs importés, " + skipped + " ignorés, classement recalculé.";
    }

    private void initClassement(League league, List<Team> t) {
        List<Ranking> rankings = new ArrayList<>();
        for (Team team : t) {
            boolean exists = rankingRepository.existsByLeagueIdAndTeamId(league.getId(),team.getId());
            if(exists){
                continue;
            }
            Ranking c = new Ranking();
            c.setLeague(league);
            c.setTeam(team);
            c.setPoints(0);
            c.setPlayed(0);
            c.setWins(0);
            c.setDraws(0);
            c.setLosses(0);
            c.setGoalsFor(0);
            c.setGoalsAgainst(0);
            c.setGoalDifference(0);
            rankings.add(c);
        }
        rankingRepository.saveAll(rankings);
    }

    private ResponseApiMatch callApi(String path) {

        try {
            return apiSportClient.get().uri(path)
                    .retrieve()
                    .body(ResponseApiMatch.class);
        } catch (RestClientException e) {

            throw new ExternalApiException("Erreur lors de l'appel API ou du parsing JSON",e);
        }
    }

    private boolean isPlayed(String statusShort) {
        return "FT".equals(statusShort) || "AET".equals(statusShort) || "PEN".equals(statusShort) || "ABD".equals(statusShort) ;
    }

    private Integer parseRoundToJournee(String round) {
        if (round == null || round.isBlank()) return null;
        int idx = round.lastIndexOf('-');
        if (idx < 0) return null;
        String n = round.substring(idx + 1).trim();
        try {
            return Integer.parseInt(n);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseToParisLocalDate(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) return null;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(isoDate);
            return odt.atZoneSameInstant(ZoneId.of("Europe/Paris")).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }


}