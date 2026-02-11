package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.player.PlayerDto;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.mapper.PlayerMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.ClubRepositoryInterface;
import com.dataFoot.ProjetData.repository.PlayersRepositoryInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayersRepositoryInterface playerRepository;
    private final ClubRepositoryInterface clubRepository;

    public PlayerService(PlayersRepositoryInterface playerRepository, ClubRepositoryInterface clubRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;
    }

    public Player createPlayer(PlayerDto dto) {
        // 1️⃣ Crée un nouvel objet joueur
        Player player = new Player();
        player.setFirstName(dto.getFirstName());
        player.setLastName(dto.getLastName());
        player.setPosition(dto.getPosition());
        player.setDateDeNaissance(dto.getDateDeNaissance());
        player.setNation(dto.getNation());

        // 2️⃣ Récupère le club correspondant à l'ID fourni
        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        // 3️⃣ Lie le joueur à ce club
        player.setClub(club);

        // 4️⃣ Sauvegarde le joueur dans la base
        return playerRepository.save(player);
    }

    public List<PlayerInClubDto> allPlayer (Long clubId){

        Optional<Club> club = clubRepository.findById(clubId);

        return playerRepository.findByClubId(club.orElseThrow().getId())
                .stream()
                .map(PlayerMapper::toInClubDto)
                .toList();
    }

    public PlayerInClubDto getPlayerById (long id){

        Player player = playerRepository.findById(id).orElseThrow(()-> new RuntimeException("lid du joueur n'esiste pas "));

        return PlayerMapper.toInClubDto(player);

    }

    public void deletePlayer(Long id){

        if(!playerRepository.existsById(id)) {
            throw (new RuntimeException("l'id n'existe pas "));
        }
            playerRepository.deleteById(id);
    }

}
