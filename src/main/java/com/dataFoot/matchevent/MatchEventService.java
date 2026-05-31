package com.dataFoot.matchevent;

import com.dataFoot.exception.entitexception.ExternalApiException;
import com.dataFoot.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.exception.entitexception.PlayerNotFoudException;
import com.dataFoot.exception.entitexception.TeamNotFoundException;
import com.dataFoot.league.League;
import com.dataFoot.league.LeagueRepository;
import com.dataFoot.match.matchdto.MatchEventDto;
import com.dataFoot.enumeration.EventType;
import com.dataFoot.match.MatchRepository;
import com.dataFoot.matchevent.dtoapi.ApiFootballEventItem;
import com.dataFoot.matchevent.dtoapi.ApiFootballEventResponse;
import com.dataFoot.matchevent.mapper.MatchEventMapper;
import com.dataFoot.matchlineup.dtoapi.ApiFootballLinupResponse;
import com.dataFoot.player.PlayersRepository;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.team.Team;
import com.dataFoot.match.Match;
import com.dataFoot.player.Player;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static com.dataFoot.matchevent.mapper.MatchEventMapper.toDto;

@Service
public class MatchEventService {

    private final MatchEventRepository eventRepo;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayersRepository playersRepository;

    private final LeagueRepository leagueRepository;
    private final RestClient apiSportClient;
    public MatchEventService(MatchEventRepository eventRepo, MatchRepository matchRepository, TeamRepository teamRepository, PlayersRepository playersRepository, LeagueRepository leagueRepository, RestClient apiSportClient) {
        this.eventRepo = eventRepo;

        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.playersRepository = playersRepository;
        this.leagueRepository = leagueRepository;
        this.apiSportClient = apiSportClient;
    }




    public List<MatchEventDto> getEventsByMatchId(Long matchId) {
        List<MatchEvent> events = eventRepo.findByMatchId(matchId);

        return events.stream()
                .map(MatchEventMapper::toDto)
                .toList();
    }

    @Transactional
    public String importEventByMatch(long leagueId){

        League league = leagueRepository.findById(leagueId).orElseThrow(()->new LeagueNotFoundException("La ligue avec l'id  : " + leagueId + " n'existe pas en base"));
        List<Match> listMatch = matchRepository.findByLeagueId(league.getId());

        int eventImported =0;

        for(Match match : listMatch){

            if (eventRepo.existsByMatchId(match.getId())) {

                continue;
            }

            String path = "fixtures/events?fixture=" + match.getApiFootballFixtureId();
            ApiFootballEventResponse response = callApi(path);
            if(response ==null || response.getResponse() == null){
                return null;
            }

            List<MatchEvent> listEvent = new ArrayList<>();

            for(ApiFootballEventItem event : response.getResponse()){
                if (event.getPlayer() == null || event.getPlayer().getId() == null) {
                    continue;
                }
                Player mainPlayer = playersRepository
                        .findByApiFootballPlayerId(event.getPlayer().getId())
                        .orElseThrow(() -> new PlayerNotFoudException(
                                "Joueur introuvable en base : id API="
                                        + event.getPlayer().getId()
                                        + ", nom="
                                        + event.getPlayer().getName()
                        ));
                Team team = teamRepository.findByApiFootballTeamId(event.getTeam().getId()).orElseThrow(()-> new TeamNotFoundException("la team n'existe pas"));

                Player playerAssist = null;
                if (event.getAssist() != null && event.getAssist().getId() != null) {
                    playerAssist = playersRepository
                            .findByApiFootballPlayerId(event.getAssist().getId())
                            .orElseThrow();
                }

                    MatchEvent matchEvent = new MatchEvent();
                    matchEvent.setMatch(match);
                    matchEvent.setMinute(event.getTime().getElapsed());
                    matchEvent.setPlayer(mainPlayer);
                    if (event.getType().equals("subst")) {
                        matchEvent.setPlayerIn(mainPlayer);
                        matchEvent.setPlayerOut(playerAssist);
                        matchEvent.setPlayerInName(event.getPlayer().getName());
                        matchEvent.setPlayerOutName(event.getAssist().getName());
                    }
                    matchEvent.setTeam(team);
                    matchEvent.setEventType(mapEventType(event.getType(), event.getDetail()));
                    matchEvent.setAssistPlayer(playerAssist);
                    if(playerAssist != null){
                        matchEvent.setAssistName(playerAssist.getName());

                    }

                    listEvent.add(matchEvent);
                    eventImported++;

                }


            eventRepo.saveAll(listEvent);
        }



        return "Evenment importés : " + eventImported;
    }

    private ApiFootballEventResponse callApi(String path) {

        try {
            return apiSportClient.get().uri(path)
                    .retrieve()
                    .body(ApiFootballEventResponse.class);
        } catch (RestClientException e) {

            throw new ExternalApiException("Erreur lors de l'appel API ou du parsing JSON",e);
        }
    }
    private EventType mapEventType(String type, String detail) {
        String t = (type == null) ? "" : type.toLowerCase();
        String d = (detail == null) ? "" : detail.toLowerCase();

        if (t.contains("goal")) {
            if (d.contains("penalty")) return EventType.PENALTY_GOAL;
            if (d.contains("own")) return EventType.OWN_GOAL;
            return EventType.GOAL;
        }

        if (t.contains("card")) {
            if (d.contains("red")) return EventType.RED_CARD;
            return EventType.YELLOW_CARD;
        }

        if (t.contains("subst")) return EventType.SUBSTITUTION;
        if (t.contains("var")) return EventType.VAR;

        return EventType.OTHER;
    }
}

