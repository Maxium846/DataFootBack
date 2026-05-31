package com.datafoot.player.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PlayerDto {

    private Long id;
    private Long clubId;
    private String name;
    private String clubName;
    private String position;
    private LocalDate dateDeNaissance;
    private String nation ;
    private Integer age;
    private String photo;
    private String leagueName;
    private String logo;
}

