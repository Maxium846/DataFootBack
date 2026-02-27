package com.dataFoot.ProjetData.model;
import com.dataFoot.ProjetData.enumeration.EventType;
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
@ToString(exclude = {"match", "player", "club"})
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
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(nullable = false)
    private int minute;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type", nullable = false, length = 30)
    private EventType eventType;


}