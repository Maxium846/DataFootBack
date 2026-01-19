package com.dataFoot.ProjetData.dto.player;

import lombok.Data;

@Data
public class PlayerInClubDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String position;
    private int age;
    private String  clubName;
}
