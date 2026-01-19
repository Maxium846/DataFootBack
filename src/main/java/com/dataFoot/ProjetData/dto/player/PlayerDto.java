package com.dataFoot.ProjetData.dto.player;

import lombok.*;

@Data
public class PlayerDto {

    public Long id;
    public String firstName;
    public String lastName;
    public String position;
    public int age;
    private Long clubId;
}
