package com.dataFoot.ProjetData.service;

import com.dataFoot.ProjetData.dto.fpl.MatchDtoFpl;
import com.dataFoot.ProjetData.dto.fpl.StatEntyDto;
import com.dataFoot.ProjetData.dto.fpl.StatFplDto;
import com.dataFoot.ProjetData.enumeration.EventType;
import com.dataFoot.ProjetData.model.*;
import com.dataFoot.ProjetData.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CalendarService {


    private final LeagueRepository leagueRepository;
    private final ClassementRepository classementRepository;

    private final ClubRepository clubRepository;

    private final MatchRepository matchRepository;

    private final FplService fplService;
     private final ClassementService classementService;
     private final PlayersRepository playersRepository;
     private final MatchEventRepository matchEventRepository;

    public CalendarService(LeagueRepository leagueRepository, ClassementRepository classementRepository, ClubRepository clubRepository, MatchRepository matchRepository, FplService fplService, MatchLineUpRepository matchLineUpRepository, ClassementService classementService, PlayersRepository playersRepository, MatchEventRepository matchEventRepository) {

        this.leagueRepository = leagueRepository;
        this.classementRepository = classementRepository;
        this.clubRepository = clubRepository;
        this.matchRepository = matchRepository;
        this.fplService = fplService;
        this.classementService = classementService;
        this.playersRepository = playersRepository;
        this.matchEventRepository = matchEventRepository;
    }




}




