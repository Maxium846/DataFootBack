package com.dataFoot.ProjetData.dto.player;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
public class PlayerDto {

    public Long id;
    public String firstName;
    public String lastName;
    public String position;
    public int age;
    private Long clubId;
    private LocalDate dateDeNaissance;
    private String nation ;
}

