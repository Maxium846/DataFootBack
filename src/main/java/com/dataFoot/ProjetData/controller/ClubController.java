package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.club.ClubDetailDto;
import com.dataFoot.ProjetData.dto.club.ClubDto;
import com.dataFoot.ProjetData.model.Club;
import com.dataFoot.ProjetData.service.ClubService;
import org.springframework.web.bind.annotation.*;
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



}
