package com.datafoot.match;

import com.datafoot.exception.entitexception.MatchNotFoundException;
import com.datafoot.match.matchdto.MatchDto;
import com.datafoot.ranking.RankingRepository;
import com.datafoot.ranking.RankingService;
import com.datafoot.team.TeamRepository;
import com.datafoot.league.LeagueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
    public class MatchService {

        private final MatchRepository matchRepo;
        private  final RankingService rankingService;
        private final MatchRepository matchRepository;

        public MatchService(MatchRepository matchRepo, RankingService rankingService, LeagueRepository leagueRepository, TeamRepository teamRepository, RankingRepository rankingRepository, MatchRepository matchRepository) {
            this.matchRepo = matchRepo;
            this.rankingService = rankingService;
            this.matchRepository = matchRepository;
        }

        public List<MatchDto> getMatchesByLeague(long leagueId){
            return matchRepository.findMatchesByLeagueIdOrderByJourneeAsc(leagueId)
                    .stream()
                    .map(MatchMapper::toDto)
                    .toList();

        }

        public MatchDto getMatchById(long id){

            Match match = matchRepository.findById(id).orElseThrow(()->new MatchNotFoundException("Le match n'existe pas en base"));
            return MatchMapper.toDto(match);
        }
}


