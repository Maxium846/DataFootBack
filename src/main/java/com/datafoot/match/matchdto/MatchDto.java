package com.datafoot.match.matchdto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchDto {

    private Long id;
    private LocalDate matchDate;

    private Long homeTeamId;
    private String homeTeamName;

    private Long awayTeamId;
    private String awayTeamName;

    private Integer homeGoals;
    private Integer awayGoals;

    private boolean played;
    private Integer journee;
}
