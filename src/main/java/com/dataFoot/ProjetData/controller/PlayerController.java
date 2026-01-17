package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import com.dataFoot.ProjetData.repository.PlayersRepositoryInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayersRepositoryInterface playersRepositoryInterface;
    private final ClubRepositoryInterface clubRepositoryInterface;


    public PlayerController(PlayersRepositoryInterface playersRepositoryInterface, ClubRepositoryInterface clubRepositoryInterface){

        this.playersRepositoryInterface= playersRepositoryInterface;

        this.clubRepositoryInterface = clubRepositoryInterface;
    }


    @GetMapping
    public List<Player> getAllPlayer(){

        return playersRepositoryInterface.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id){

        return  playersRepositoryInterface.findById(id).map(player -> ResponseEntity.ok(player)).orElse(ResponseEntity.notFound().build());

    }
    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {

        // Vérifier que le club existe
        if (player.getClub() != null && player.getClub().getId() != null) {
            Optional<Club> club = clubRepositoryInterface.findById(player.getClub().getId());
            if (club.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            player.setClub(club.get());
        }

        Player saved = playersRepositoryInterface.save(player);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable Long id,
            @RequestBody Player player) {

        if (!playersRepositoryInterface.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        player.setId(id);

        if (player.getClub() != null && player.getClub().getId() != null) {
            Optional<Club> club = clubRepositoryInterface.findById(player.getClub().getId());
            if (club.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            player.setClub(club.get());
        }

        Player updated = playersRepositoryInterface.save(player);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id){

        if(playersRepositoryInterface.existsById(id)){

            playersRepositoryInterface.deleteById(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}

