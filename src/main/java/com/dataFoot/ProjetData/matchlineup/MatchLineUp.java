package com.dataFoot.ProjetData.matchlineup;

import com.dataFoot.ProjetData.enumeration.Position;
import com.dataFoot.ProjetData.match.Match;
import com.dataFoot.ProjetData.player.Player;
import com.dataFoot.ProjetData.team.Teams;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "match_lineup",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_match_lineup_match_player",
                columnNames = {"match_id", "player_id"}
        ),
        indexes = {
                @Index(name="idx_lineup_match", columnList="match_id"),
                @Index(name="idx_lineup_player", columnList="player_id"),
                @Index(name="idx_lineup_club", columnList="team_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"match","player","teams"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MatchLineUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="match_id", nullable=false)
    private Match match;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="player_id", nullable=false)
    private Player player;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable=false)
    private Teams teams;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Position position;

    @Column(nullable=false)
    private boolean starter;
}