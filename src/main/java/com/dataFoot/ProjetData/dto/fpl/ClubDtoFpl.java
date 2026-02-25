package com.dataFoot.ProjetData.dto.fpl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubDtoFpl {
    @JsonProperty("id")
    private  Long id;
    @JsonProperty("name")
    private String name;
    private Long leagueId;
}
