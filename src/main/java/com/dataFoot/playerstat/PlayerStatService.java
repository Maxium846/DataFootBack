package com.dataFoot.playerstat;
import com.dataFoot.exception.entitexception.ExternalApiException;
import com.dataFoot.playerstat.dtoapi.ApiFootballPlayerStatItems;
import com.dataFoot.playerstat.dtoapi.ApiFootballPlayerStatPlayers;
import com.dataFoot.playerstat.dtoapi.ApiFootballPlayerStatResponse;
import com.dataFoot.playerstat.dtoapi.ApiFootballPlayerStatStatistique;
import com.dataFoot.playerstat.playerstatdto.PlayerStatImpactDto;
import com.dataFoot.playerstat.playerstatdto.PlayerStatOffensiveDto;
import com.dataFoot.playerstat.playerstatdto.PlayerStatPasseDto;
import com.dataFoot.team.Team;
import com.dataFoot.match.Match;
import com.dataFoot.player.Player;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.match.MatchRepository;
import com.dataFoot.player.PlayersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerStatService {


    private final PlayerStatRepository playerStatRepository;
    private final PlayersRepository playersRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final RestClient apiSportClient;


    public PlayerStatService(PlayerStatRepository playerStatRepository, PlayersRepository playersRepository, MatchRepository matchRepository, TeamRepository teamRepository, RestClient apiSportClient) {
        this.playerStatRepository = playerStatRepository;
        this.matchRepository = matchRepository;
        this.playersRepository = playersRepository;
        this.teamRepository = teamRepository;
        this.apiSportClient = apiSportClient;
    }


    public List<PlayerStatOffensiveDto> getStat(Long leagueId) {

        return playerStatRepository.findTop30PlayerStatsByLeagueId(leagueId);
    }

    public List<PlayerStatPasseDto>getStatPasseur(Long leagueId){

        return playerStatRepository.findPlayerStatsPasseByLeagueId(leagueId);
    }

    public List<PlayerStatImpactDto> getStatsInpact (Long leagueId){

        return playerStatRepository.findPlayerStatsImpactByLeagueId(leagueId);
    }

    @Transactional
    public int importStatPlayer(Long leagueId){

        List<Match> matches = matchRepository.findByLeagueId(leagueId);

        int nbrStat = 0;
        for(Match match : matches){
            List<PlayerStats> listStat = new ArrayList<>();


            if (playerStatRepository.existsByMatchId(match.getId())){
                continue;
            }
            String path = "/fixtures/players?fixture=" + match.getApiFootballFixtureId();

            ApiFootballPlayerStatResponse response = callApi(path);

            for(ApiFootballPlayerStatItems playerStat : response.getResponse()){


                Team team = teamRepository.findByApiFootballTeamId(playerStat.getTeam().getId())
                        .orElseThrow();

                for(ApiFootballPlayerStatPlayers stat : playerStat.getPlayers()){


                    Player player = playersRepository
                            .findByApiFootballPlayerId(stat.getPlayer().getId())
                            .orElseGet(() -> {
                                Player newPlayer = new Player();

                                newPlayer.setApiFootballPlayerId(stat.getPlayer().getId());
                                newPlayer.setName(stat.getPlayer().getName());
                                newPlayer.setPhoto(stat.getPlayer().getPhoto());

                                return playersRepository.save(newPlayer);
                            });

                    for(ApiFootballPlayerStatStatistique statPlayer : stat.getStatistics()){

                        PlayerStats playerStats = new PlayerStats();

                        playerStats.setMatch(match);
                        playerStats.setTeam(team);
                        playerStats.setNameClub(playerStat.getTeam().getName());

                        playerStats.setPlayers(player);
                        playerStats.setNameJoueur(player.getName());

                        playerStats.setAssist(statPlayer.getGoals().getAssists());

                        Integer accuracyInt = null;
                        if(statPlayer.getPasses().getAccuracy() != null
                                && !statPlayer.getPasses().getAccuracy().isBlank()){
                            accuracyInt = Integer.parseInt(statPlayer.getPasses().getAccuracy());
                        }

                        playerStats.setAccuracyPass(accuracyInt);
                        playerStats.setAttemptsDribbles(statPlayer.getDribbles().getAttempts());
                        playerStats.setBlocks(statPlayer.getTackles().getBlocks());
                        playerStats.setCaptain(statPlayer.getGames().isCaptain());
                        playerStats.setFoulsCommitted(statPlayer.getFouls().getCommitted());
                        playerStats.setFoulsDrawns(statPlayer.getFouls().getDrawn());
                        playerStats.setGoalConceded(statPlayer.getGoals().getConceded());
                        playerStats.setInterception(statPlayer.getTackles().getInterception());
                        playerStats.setKeyPasse(statPlayer.getPasses().getKey());
                        playerStats.setMinutePlayed(statPlayer.getGames().getMinutes());
                        playerStats.setNote(statPlayer.getGames().getRating());
                        playerStats.setOffside(statPlayer.getOffside());
                        playerStats.setPastDribbles(statPlayer.getDribbles().getPast());
                        playerStats.setPenaltSaved(statPlayer.getPenalty().getSaved());
                        playerStats.setPenaltyCommited(statPlayer.getPenalty().getCommitted());
                        playerStats.setPenaltyMissed(statPlayer.getPenalty().getMissed());
                        playerStats.setPenaltyScored(statPlayer.getPenalty().getScored());
                        playerStats.setPenaltyWon(statPlayer.getPenalty().getWon());
                        playerStats.setRedCard(statPlayer.getCards().getRed());
                        playerStats.setSaves(statPlayer.getGoals().getSave());
                        playerStats.setShootOnTarget(statPlayer.getShots().getOn());
                        playerStats.setSubstitute(statPlayer.getGames().isSubstitute());
                        playerStats.setSucessDribles(statPlayer.getDribbles().getSuccess());
                        playerStats.setTotalDuels(statPlayer.getDuels().getTotal());
                        playerStats.setTotalGoal(statPlayer.getGoals().getTotal());
                        playerStats.setTotalPasse(statPlayer.getPasses().getTotal());
                        playerStats.setTotalShoot(statPlayer.getShots().getTotal());
                        playerStats.setTotalTackle(statPlayer.getTackles().getTotal());
                        playerStats.setWonDuels(statPlayer.getDuels().getWon());

                        listStat.add(playerStats);
                    }
                }
            }
            playerStatRepository.saveAll(listStat);
            nbrStat += listStat.size();

        }

        return nbrStat;

    }

    private ApiFootballPlayerStatResponse callApi(String path) {

        try {
            ApiFootballPlayerStatResponse response = apiSportClient.get()
                    .uri(path)
                    .retrieve()
                    .body(ApiFootballPlayerStatResponse.class);

            if(response == null || response.getResponse() == null){
                throw new ExternalApiException("Réponse API vide pour : " + path);
            }

            return response;

        } catch (RestClientException e) {
            throw new ExternalApiException(
                    "Erreur lors de l'appel API ou du parsing JSON",
                    e
            );
        }
    }

}


