package com.dataFoot.ProjetData.dto.player;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PlayerInClubDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String position;
    private int age;
    private String  clubName;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateDeNaissance;
    private String nation;
}
