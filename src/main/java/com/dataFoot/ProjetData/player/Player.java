package com.dataFoot.ProjetData.player;

import com.dataFoot.ProjetData.enumeration.Position;
import com.dataFoot.ProjetData.matchlineup.MatchLineUp;
import com.dataFoot.ProjetData.playerstat.PlayerStats;
import com.dataFoot.ProjetData.team.Teams;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"teams", "matchLineUps"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    @Column
    private Position position;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Teams teams;
    private String nation;
    private LocalDate birthday;
    @OneToMany(mappedBy = "player")
    private List<PlayerStats> playerStats;
    @OneToMany(mappedBy = "player")
    private List<MatchLineUp> matchLineUps = new ArrayList<>();
    @Column(unique = true)
    private Integer apiFootballPlayerId;
    private Integer number;
    private String size;
    private String weight;
    @Column(name = "photo")
    private String photo;

}
