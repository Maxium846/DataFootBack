package com.dataFoot.ProjetData.dto.player;

import com.dataFoot.ProjetData.enumeration.Position;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PlayerDto {

    private Long id;
    private Long clubId;
    private String firstName;
    private String clubName;
    private String position;
    private LocalDate dateDeNaissance;
    private String nation ;
    private Integer age;
    private String photo;
    private String leagueName;
    private String logo;
}

