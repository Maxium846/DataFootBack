package com.dataFoot.ProjetData.controller;

import com.dataFoot.ProjetData.dto.classement.ClassementDto;
import com.dataFoot.ProjetData.dto.classement.ClassementDtoPageAccueil;
import com.dataFoot.ProjetData.mapper.ClassementMapper;
import com.dataFoot.ProjetData.model.Classement;
import com.dataFoot.ProjetData.service.ClassementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
public class ClassementController {

    private final ClassementService classementService;

    public ClassementController(ClassementService classementService) {
        this.classementService = classementService;
    }

    //ok
    @GetMapping("/{leagueId}/classement")
    public List<ClassementDto> getClassement(@PathVariable Long leagueId){
        return classementService.getClassementByLeague(leagueId);
    }

    @GetMapping("/classementAccueil")
    public List<ClassementDtoPageAccueil>pageAccueilClassemen(){

        return classementService.pageAccueilClassement();
    }

}



