package com.dataFoot.ProjetData.dto.player;

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

    private Integer fplId;

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("second_name")
    private String secondName;
    @JsonProperty("element_type")
    private String position ;
    @JsonProperty("team")
    private Long teamId;
    @JsonProperty("birth_date")
    private LocalDate dateDeNaissance;

}
