package com.datafoot.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDtoApi {
    private  Long id;
    private String name;
    private Long leagueId;
    private int dateFondation;
    private String entraineur;
    private String logo;

}
