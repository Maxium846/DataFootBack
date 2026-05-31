package com.datafoot.player;

import com.datafoot.enumeration.Position;
import com.datafoot.exception.entitexception.ExternalApiException;
import com.datafoot.exception.entitexception.LeagueNotFoundException;
import com.datafoot.league.League;
import com.datafoot.player.dto.PlayerApiDto;
import com.datafoot.player.dtoplayerprofilapi.PlayerProfilItem;
import com.datafoot.player.dtoplayerprofilapi.PlayerProfilResponse;
import com.datafoot.player.dtoplayersquadapi.PlayerItemApi;
import com.datafoot.player.dtoplayersquadapi.PlayerItemPlayersApi;
import com.datafoot.player.dtoplayersquadapi.PlayerResponseApi;
import com.datafoot.player.mapper.PlayerMapper;
import com.datafoot.team.Team;
import com.datafoot.team.TeamRepository;
import com.datafoot.league.LeagueRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlayerImportService {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final PlayersRepository playerRepository;
    private final RestClient apiSportClient;

    public PlayerImportService(LeagueRepository leagueRepository,
                               TeamRepository teamRepository,
                               PlayersRepository playerRepository,
                               RestClient apiSportClient) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.apiSportClient = apiSportClient;
    }

    @Transactional
    public List<PlayerApiDto> importOrUpdatePlayers(Long leagueId) throws Exception {

        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new LeagueNotFoundException(" la League " + leagueId + " est inexistante"));

        List<Team> teamList = teamRepository.findByLeagueId(league.getId());
        List<Player> playersToSave = new ArrayList<>();

        // Cache pour éviter les doublons
        Set<Integer> dejaImportes = new HashSet<>();

        for (Team team : teamList) {

            Long apiTeamId = team.getApiFootballTeamId();

            PlayerResponseApi squad = callApiWithRetry(
                    "/players/squads?team=" + apiTeamId,
                    PlayerResponseApi.class
            );

            for (PlayerItemApi item : squad.getResponse()) {
                for (PlayerItemPlayersApi p : item.getPlayers()) {


                    if (!dejaImportes.add(p.getId())) continue;

                    Player player = playerRepository.findByApiFootballPlayerId(p.getId())
                            .orElseGet(Player::new);

                    PlayerProfilResponse profil = callApiWithRetry(
                            "/players/profiles?player=" + p.getId(),
                            PlayerProfilResponse.class
                    );

                    for (PlayerProfilItem playerProfilItem : profil.getResponse()) {


                        Player result = PlayerMapper.toUpdateEntity(player, p, playerProfilItem, team, mapPosition(p.getPosition()));

                        playersToSave.add(result);
                    }
                }
            }
        }


        playerRepository.saveAll(playersToSave);
        return playersToSave.stream().map(PlayerMapper::toDtoApi).toList();
    }


    private <T> T callApiWithRetry(String path, Class<T> clazz) throws Exception {

        int maxRetries = 8;
        long waitMs = 200;
        long backoffMs = 1200;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            try {

                T result = apiSportClient.get()
                        .uri(path)
                        .retrieve()
                        .body(clazz);
                if (result instanceof PlayerResponseApi r && (r.getResponse() == null || r.getResponse().isEmpty())) {

                    throw new ExternalApiException("Réponse de L'api Vide",null);
                }
                if (result instanceof PlayerProfilResponse r2 &&
                        (r2.getResponse() == null || r2.getResponse().isEmpty())) {
                    throw new ExternalApiException("Réponse vide de l'API",null);
                }

                return result;
            } catch (HttpClientErrorException.TooManyRequests e) {
                Thread.sleep(backoffMs);
                backoffMs = Math.min(backoffMs * 2, 20000);
                continue;

            } catch (Exception e) {
                // autres erreurs HTTP
                if (attempt == maxRetries) {
                    throw new ExternalApiException("Erreur API après retries : " + e.getMessage(), e);
                }
                Thread.sleep(waitMs);
            }
        }

        throw new ExternalApiException("Impossible d'appeler l'API après retries");
    }

    public Position mapPosition(String pos) {
        if (pos == null || pos.isBlank()) return null;
        return switch (pos) {
            case "Goalkeeper" -> Position.GK;
            case "Defender" -> Position.DEF;
            case "Midfielder" -> Position.MID;
            case "Attacker" -> Position.FWD;
            default -> null;
        };
    }
}




