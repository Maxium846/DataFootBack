package com.dataFoot.ProjetData.model;

import com.dataFoot.ProjetData.enumeration.Position;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"club", "matchLineUps"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position;
    @ManyToOne
    @JoinColumn(name = "club_id",nullable = false)
    private Club club;
    @Column(nullable = true)
    private String nation;
    @Column(nullable = true)
    private LocalDate dateDeNaissance;
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL,orphanRemoval=true)

    private PlayerStats playerStats;
    @Transient
    public Integer getAge(){
        if(dateDeNaissance == null) return null;

        return Period.between(this.dateDeNaissance,LocalDate.now()).getYears();
    }
    @OneToMany(mappedBy = "player")
    private List<MatchLineUp> matchLineUps = new ArrayList<>();
    @Column(unique = true)
    private Integer apiFootballPlayerId;
}
