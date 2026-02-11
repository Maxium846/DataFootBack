package com.dataFoot.ProjetData.dto.club;

import lombok.*;

import java.util.Date;

@Data
public class ClubDto {
    public Long id;
    public String name;
    public Long leagueId;
    public String president;
    public String entraineur;
    public Integer dateCreation;
}
