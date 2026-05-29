package com.dataFoot.ProjetData.team.teamdto;

import lombok.*;

@Data

public class TeamDto {
    public Long id;
    public String name;
    public Long leagueId;
    private Integer fondation;
    private String logo;
    private  String image;
    private String city;
    private String surface;
    private String nameStadium;

}
