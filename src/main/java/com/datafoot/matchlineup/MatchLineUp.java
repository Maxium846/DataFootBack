package com.datafoot.matchlineup;

import com.datafoot.enumeration.Position;
import com.datafoot.match.Match;
import com.datafoot.player.Player;
import com.datafoot.team.Team;
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
@ToString(exclude = {"match","players","team"})
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
    private Player players;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable=false)
    private Team team;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Column(nullable=false)
    private boolean starter;
}