package com.dataFoot.ProjetData.model;

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
@ToString(exclude = "club")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String position;
    @ManyToOne
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;
    @Column(nullable = true)
    private String nation;
    @Column(nullable = true)
    private LocalDate dateDeNaissance;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    private PlayerStats playerStats;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public int getAge(){

        return Period.between(this.dateDeNaissance,LocalDate.now()).getYears();
    }
    @OneToMany(mappedBy = "player")
    private List<MatchLineUp> matchLineUps = new ArrayList<>();

    @OneToMany(mappedBy = "player")
    private List<MatchEvent> events = new ArrayList<>();

    @OneToMany(mappedBy = "assistPlayer")
    private List<MatchEvent> assists = new ArrayList<>();

}
