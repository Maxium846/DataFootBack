package com.dataFoot.ProjetData.dto.player;

import com.dataFoot.ProjetData.enumeration.Position;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerFplDto {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("team")
    private Long teamId;

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("second_name")
    private String lastName;
    @JsonProperty("element_type")
    private Position position ;
    @JsonProperty("birth_date")
    private LocalDate dateDeNaissance;
    private  String nation;
    private Integer ApiFootballPlayerId;


}
