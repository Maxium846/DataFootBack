package com.dataFoot.ProjetData.model;

import com.dataFoot.ProjetData.enumeration.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long matchId;
    private Long playerId;
    private int minute;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private Long clubId;

}
