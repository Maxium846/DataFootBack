package com.dataFoot.ProjetData.team.teamdto;

import com.dataFoot.ProjetData.player.dto.PlayerInClubDto;
import lombok.*;

import java.util.List;
@Data
public class TeamDetailDto {

    public Long id;
    public String name;
    public List<PlayerInClubDto> player;

}
