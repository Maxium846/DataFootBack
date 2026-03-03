package com.dataFoot.ProjetData.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubDtoApi {
    private  Long id;
    private String name;
    private Long leagueId;
    private int dateFondation;
    private String entraineur;
}
