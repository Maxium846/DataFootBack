package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.club.ClubDetailDto;
import com.dataFoot.ProjetData.dto.club.ClubDto;
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


    @PostMapping
    public ClubDto create(@RequestBody ClubDto dto) {
        return clubService.create(dto);
    }
    @PutMapping("/{id}")
    public ClubDto update(@PathVariable Long id, @RequestBody ClubDto dto) {
        return clubService.update(id, dto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clubService.delete(id);
    }

    @GetMapping("/{id}")
    public ClubDetailDto getClubDetailById(@PathVariable Long id) {
        return clubService.getClubDetail(id);
    }



}
