package com.dataFoot.ProjetData.dto.league;

import com.dataFoot.ProjetData.dto.club.ClubDto;
import lombok.Data;

import java.util.List;

@Data
public class LeagueByIdDto {


    private long id;
    private String name;
    private String country;
    private List<ClubDto> clubs;
}
