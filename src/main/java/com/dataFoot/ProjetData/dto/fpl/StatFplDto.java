package com.dataFoot.ProjetData.dto.fpl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data

public class StatFplDto {
        @JsonProperty("identifier")
        private String identifier;   // ex: "goals_scored", "assists", "yellow_cards"...

        @JsonProperty("h")
        private List<StatEntyDto> h; // home

        @JsonProperty("a")
        private List<StatEntyDto> a; // away
    }


