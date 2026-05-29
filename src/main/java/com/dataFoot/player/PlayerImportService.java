package com.dataFoot.player;

import com.dataFoot.enumeration.Position;
import com.dataFoot.exception.entitexception.ExternalApiException;
import com.dataFoot.exception.entitexception.LeagueNotFoundException;
import com.dataFoot.league.League;
import com.dataFoot.player.dto.PlayerApiDto;
import com.dataFoot.player.dtoplayerprofilapi.PlayerProfilItem;
import com.dataFoot.player.dtoplayerprofilapi.PlayerProfilResponse;
import com.dataFoot.player.dtoplayersquadapi.PlayerItemApi;
import com.dataFoot.player.dtoplayersquadapi.PlayerItemPlayersApi;
import com.dataFoot.player.dtoplayersquadapi.PlayerResponseApi;
import com.dataFoot.player.mapper.PlayerMapper;
import com.dataFoot.team.Teams;
import com.dataFoot.team.TeamRepository;
import com.dataFoot.league.LeagueRepository;
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

        List<Teams> teamsList = teamRepository.findByLeagueId(league.getId());
        List<Player> playersToSave = new ArrayList<>();

        // Cache pour éviter les doublons
        Set<Integer> dejaImportes = new HashSet<>();

        for (Teams team : teamsList) {

            Long apiTeamId = team.getApiFootballTeamId();

            PlayerResponseApi squad = callApiWithRetry(
                    "/players/squads?team=" + apiTeamId,
                    PlayerResponseApi.class
            );

            for (PlayerItemApi item : squad.getResponse()) {
                for (PlayerItemPlayersApi p : item.getPlayers()) {

                    // Déjà importé ? On skip

                    if (!dejaImportes.add(p.getId())) continue;

                    Player player = playerRepository.findByApiFootballPlayerId(p.getId())
                            .orElseGet(Player::new);

                    // Profil complet avec retry
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

    // ============================
    //   RETRY + BACKOFF EXPONENTIEL
    // ============================

    private <T> T callApiWithRetry(String path, Class<T> clazz) throws Exception {

        int maxRetries = 8;
        long waitMs = 200;       // si erreur non 429 petit délai entre les appels( evite le spam API )
        long backoffMs = 1200;   // délai en cas de 429

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
                // 429 → on attend et on réessaie
                Thread.sleep(backoffMs);
                // Si L'API renvoie un 429 on double le temps d'appel entre les retry
                backoffMs = Math.min(backoffMs * 2, 20000);
                continue;

            } catch (Exception e) {
                // autres erreurs HTTP
                if (attempt == maxRetries) {
                    throw new ExternalApiException("Erreur API après retries : " + e.getMessage(), e);
                }
                // Fais une pause de 200 ms si l'erreur n'est pas une 429 (500 , 502 , 503 etc )
                Thread.sleep(waitMs);
            }
        }

        throw new RuntimeException("Impossible d'appeler l'API après retries");
    }

    public Position mapPosition(String pos) {
        return switch (pos) {
            case "Goalkeeper" -> Position.GK;
            case "Defender" -> Position.DEF;
            case "Midfielder" -> Position.MID;
            case "Attacker" -> Position.FWD;
            default -> null;
        };
    }
}




