package com.dataFoot.ProjetData.dto;

import com.dataFoot.ProjetData.model.Player;
import lombok.*;

import java.util.List;
@Data
public class ClubDetailDto {

    public Long id;
    public String name;
    public String country;
    public String league;
    public List<PlayerDto> player;


}
