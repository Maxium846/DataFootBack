package com.dataFoot.ProjetData.model;

import com.dataFoot.ProjetData.enumeration.Position;
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
                @Index(name="idx_lineup_club", columnList="club_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"match","player","club"})
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
    @JoinColumn(name="club_id", nullable=false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Position position;

    @Column(nullable=false)
    private boolean starter;
}