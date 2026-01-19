package com.dataFoot.ProjetData.dto.league;

import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.model.Club;
import lombok.Data;

import java.util.List;

@Data
public class LeagueDetailDto {


    private long id;
    private String name;
    private String country;
    private List<ClubDto> clubs;
}
