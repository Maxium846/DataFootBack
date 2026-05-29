package com.dataFoot.team;

import com.dataFoot.league.League;
import com.dataFoot.ranking.Ranking;
import com.dataFoot.player.Player;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"players", "rankings"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity

@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    @OneToMany(mappedBy = "team")
    private List<Ranking> rankings = new ArrayList<>();

    private Integer founded;

    @Column(unique = true)
    private Long apiFootballTeamId;
    @Column(name = "logo")
    private String logo;
    private String nameStadium;
    private String city;
    private String surface;
    private String image;
}
