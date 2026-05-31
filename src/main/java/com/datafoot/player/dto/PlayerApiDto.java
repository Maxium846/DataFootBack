package com.datafoot.player.dto;

import com.datafoot.enumeration.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerApiDto {

    private Long id;
    private Long teamId;
    private String name;
    private Position position ;
    private LocalDate dateDeNaissance;
    private  String nation;
    private Integer apiFootballPlayerId;
    private int number;
    private String photo;
    private String size;
    private String weight;
    private LocalDate birthday;


}
