package com.datafoot.player.mapper;

import com.datafoot.enumeration.Position;
import com.datafoot.player.dto.PlayerApiDto;
import com.datafoot.player.dto.PlayerDto;
import com.datafoot.player.dto.PlayerInClubDto;
import com.datafoot.player.Player;
import com.datafoot.player.dtoplayerprofilapi.PlayerProfilItem;
import com.datafoot.player.dtoplayersquadapi.PlayerItemPlayersApi;
import com.datafoot.team.Team;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PlayerMapper {


    public static PlayerInClubDto toInClubDto(Player player) {
        PlayerInClubDto dto = new PlayerInClubDto();
        dto.setId(player.getId());

        if (player.getBirthday() != null){
            dto.setDateDeNaissance(player.getBirthday());
        }
        if (player.getPosition() != null) {

            dto.setPosition(player.getPosition().name());

        }
        dto.setFirstName(player.getName());
        dto.setAge(player.getBirthday() != null
                ? (int) player.getBirthday().until(LocalDate.now(), ChronoUnit.YEARS)
                : 0); // ou null si ton champ peut être Integer
        if (player.getTeam() != null) {
            dto.setClubName(player.getTeam().getName());
        }
        dto.setNation(player.getNation());
        dto.setPhoto(player.getPhoto());
        dto.setLeagueName(player.getTeam().getLeague().getName());
        return dto;
    }

    public static PlayerDto toDto(Player player) {
        PlayerDto dto = new PlayerDto();
        dto.setId(player.getId());
        if(player.getTeam() != null){
            dto.setClubId(player.getTeam().getId());

        }

        if (player.getBirthday() != null){
            dto.setDateDeNaissance(player.getBirthday());
        }
        if (player.getPosition() != null) {

            dto.setPosition(player.getPosition().name());

        }
        dto.setAge(player.getBirthday() != null
                ? (int) player.getBirthday().until(LocalDate.now(), ChronoUnit.YEARS)
                : 0); // ou null si ton champ peut être Integer
        if (player.getTeam() != null) {
            dto.setClubName(player.getTeam().getName());
        }
        dto.setNation(player.getNation());
        dto.setPhoto(player.getPhoto());
        if(player.getTeam() != null){
            dto.setLeagueName(player.getTeam().getLeague().getName());
        }
        if(player.getTeam() != null) {

            dto.setLogo(player.getTeam().getLogo());
        }
        if(player.getName() != null){
            dto.setName(player.getName());
        }
        return dto;
    }

    public static PlayerApiDto toDtoApi(Player player) {
        PlayerApiDto dto = new PlayerApiDto();
        dto.setTeamId(player.getTeam().getId());
        dto.setNumber(player.getNumber());
        dto.setSize(player.getSize());
        dto.setWeight(player.getWeight());
        dto.setName(player.getName());
        dto.setNation(player.getNation());
        dto.setPhoto(player.getPhoto());
        dto.setApiFootballPlayerId(player.getApiFootballPlayerId());
        dto.setDateDeNaissance(player.getBirthday());
        dto.setId(player.getId());

        return dto;
    }
    public static Player toUpdateEntity(Player player , PlayerItemPlayersApi playerItemPlayersApi , PlayerProfilItem playerProfilItem, Team team, Position position){

        player.setApiFootballPlayerId(playerItemPlayersApi.getId());
        player.setName(playerItemPlayersApi.getName());
        player.setPosition(position);
        player.setNumber(playerItemPlayersApi.getNumber());
        player.setWeight(playerProfilItem.getPlayer().getWeight());
        player.setSize(playerProfilItem.getPlayer().getHeight());
        String date = playerProfilItem.getPlayer().getBirth().getDate();
        if (date != null && date.length() >= 10) {
            player.setBirthday(LocalDate.parse(date.substring(0, 10)));
        }
        player.setNation(playerProfilItem.getPlayer().getNationality());
        player.setPhoto(playerItemPlayersApi.getPhoto());
        player.setTeam(team);
        return player;

    }

    public static Player toEntity (PlayerDto dto){

        Player player = new Player();
        player.setId(dto.getId());
        player.setBirthday(dto.getDateDeNaissance());
        return player;
    }
}
