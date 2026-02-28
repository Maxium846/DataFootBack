package com.dataFoot.ProjetData.service;
import com.dataFoot.ProjetData.dto.player.PlayerInClubDto;
import com.dataFoot.ProjetData.mapper.PlayerMapper;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.model.Player;
import com.dataFoot.ProjetData.repository.ClubRepository;
import com.dataFoot.ProjetData.repository.PlayersRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayersRepository playerRepository;
    private final ClubRepository clubRepository;



    public PlayerService(PlayersRepository playerRepository, ClubRepository clubRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;

    }



    public List<PlayerInClubDto> allPlayer (Long clubId){

        Optional<Club> club = clubRepository.findById(clubId);

        return playerRepository.findByClubId(club.orElseThrow().getId())
                .stream()
                .map(PlayerMapper::toInClubDto)
                .toList();
    }

    public PlayerInClubDto getPlayerById (long id){

        Player player = playerRepository.findById(id).orElseThrow(()-> new RuntimeException("l'id du joueur n'esiste pas "));

        return PlayerMapper.toInClubDto(player);

    }


}
