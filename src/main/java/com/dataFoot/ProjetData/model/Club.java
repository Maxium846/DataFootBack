package com.dataFoot.ProjetData.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"player", "classements"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity

@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Relation avec joueurs
    @OneToMany(mappedBy = "club")
    @JsonManagedReference
    private List<Player> player = new ArrayList<>();

    // Relation avec League
    @ManyToOne(optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    private String president;
    private String entraineur;

    // Relation avec Classement
    @OneToMany(mappedBy = "club")
    private List<Classement> classements = new ArrayList<>();
}
