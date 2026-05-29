package com.dataFoot.team.teamdto;

import com.dataFoot.player.dto.PlayerInClubDto;
import lombok.*;

import java.util.List;
@Data
public class TeamDetailDto {

    public Long id;
    public String name;
    public List<PlayerInClubDto> player;

}
