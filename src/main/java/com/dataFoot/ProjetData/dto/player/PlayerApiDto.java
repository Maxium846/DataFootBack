package com.dataFoot.ProjetData.dto.player;

import com.dataFoot.ProjetData.enumeration.Position;
import com.dataFoot.ProjetData.model.Club;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerApiDto {

    private Long id;
    private Long teamId;
    private String firstName;
    private String lastName;
    private Position position ;
    private LocalDate dateDeNaissance;
    private  String nation;
    private Integer apiFootballPlayerId;

    public PlayerApiDto(Integer number, Long id, Position position, Integer apiFootballPlayerId, String firstName) {
    }


}
