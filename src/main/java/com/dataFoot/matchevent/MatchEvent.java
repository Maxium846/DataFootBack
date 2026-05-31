package com.dataFoot.matchevent;
import com.dataFoot.enumeration.EventType;
import com.dataFoot.match.Match;
import com.dataFoot.player.Player;
import com.dataFoot.team.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_events",
        indexes = {
                @Index(name = "idx_match_events_match", columnList = "match_id"),
                @Index(name = "idx_match_events_player", columnList = "player_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"match", "player", "assistPlayer", "team", "playerOut", "playerIn"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assist_player_id")
    private Player assistPlayer;

    @Column(name = "assist_name")
    private String assistName;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private int minute;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type", nullable = false, length = 30)
    private EventType eventType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_out_id")
    private Player playerOut;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_in_id")
    private Player playerIn;
    @Column(name = "player_out_name")
    private String playerOutName;
    @Column(name = "player_in_name")
    private String  playerInName;


}