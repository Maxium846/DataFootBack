package com.datafoot.player;

import com.datafoot.enumeration.Position;
import com.datafoot.matchlineup.MatchLineUp;
import com.datafoot.playerstat.PlayerStats;
import com.datafoot.team.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"team", "matchLineUps"})
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
    private Team team;
    private String nation;
    private LocalDate birthday;
    @OneToMany(mappedBy = "players")
    private List<PlayerStats> playerStats;
    @OneToMany(mappedBy = "players")
    private List<MatchLineUp> matchLineUps = new ArrayList<>();
    @Column(unique = true)
    private Integer apiFootballPlayerId;
    private Integer number;
    private String size;
    private String weight;
    @Column(name = "photo")
    private String photo;

}
