package com.dataFoot.player.dtoplayerprofilapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerProfilPlayer {

    private long id;
    private String name;

    private PlayerProfilBirth birth;
    private String nationality;
    private String height;
    private String weight;
    private String photo;
}
