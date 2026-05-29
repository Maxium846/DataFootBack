package com.dataFoot.ProjetData.player.dtoplayersquadapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayerItemApi {

    private PlayerItemTeamApi team;
    private List<PlayerItemPlayersApi> players;
}
