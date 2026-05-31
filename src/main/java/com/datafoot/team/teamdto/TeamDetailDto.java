package com.datafoot.team.teamdto;

import com.datafoot.player.dto.PlayerInClubDto;
import lombok.*;

import java.util.List;
@Data
public class TeamDetailDto {

    public Long id;
    public String name;
    public List<PlayerInClubDto> player;

}
