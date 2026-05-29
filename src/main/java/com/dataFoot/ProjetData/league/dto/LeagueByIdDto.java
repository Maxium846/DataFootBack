package com.dataFoot.ProjetData.league.dto;

import com.dataFoot.ProjetData.team.teamdto.TeamDto;
import lombok.Data;

import java.util.List;

@Data
public class LeagueByIdDto {


    private long id;
    private String name;
    private String country;
    private List<TeamDto> clubs;
}
