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
@ToString(exclude = "player")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "club",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Player> player = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

}
