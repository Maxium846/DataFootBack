package com.dataFoot.ProjetData.dto.club;

import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import lombok.*;

import java.util.List;
@Data
public class ClubDetailDto {

    public Long id;
    public String name;
    public List<PlayerInClubDto> player;



}
