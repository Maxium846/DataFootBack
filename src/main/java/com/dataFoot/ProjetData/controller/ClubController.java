package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.dto.fpl.ClubDtoFpl;
import com.dataFoot.ProjetData.service.ClubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {
    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }


    @GetMapping
    public List<ClubDto> getAllClubs() {
        return clubService.findAll();
    }

    @GetMapping("/{id}")
    public ClubDto getClubsById(@PathVariable Long id){return clubService.findById(id);}
    @PostMapping
    public ClubDto create(@RequestBody ClubDto dto) {
        return clubService.createClub(dto);
    }


    @PutMapping("/{id}")
    public ClubDto update(@PathVariable Long id, @RequestBody ClubDto dto) {
        return clubService.update(id, dto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clubService.delete(id);
    }

    @PostMapping("/generate-clubs/{leagueId}/{saison}")
    public ResponseEntity<List<ClubDtoFpl>> generateClubs(@PathVariable Long leagueId, @PathVariable int saison) {
        try {
            List<ClubDtoFpl> dtos = clubService.generateOrUpdateClubsApiFootball(leagueId,saison);
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }





}
