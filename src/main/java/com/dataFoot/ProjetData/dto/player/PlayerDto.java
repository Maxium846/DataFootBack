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
    private String lastName;
    private Position position;
    private LocalDate dateDeNaissance;
    private String nation ;
    private Integer fplId;
}

