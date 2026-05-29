package com.dataFoot.ProjetData.team;

import com.dataFoot.ProjetData.league.League;
import com.dataFoot.ProjetData.ranking.Ranking;
import com.dataFoot.ProjetData.player.Player;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"player", "rankings"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity

@Table(name = "teams")
public class Teams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "teams")
    private List<Player> player = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    @OneToMany(mappedBy = "teams")
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
