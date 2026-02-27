package com.dataFoot.ProjetData.dto.fpl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StatEntyDto {

    @JsonProperty("element")
    private Integer playerFplId;

    @JsonProperty("value")
    private Integer value;
}

