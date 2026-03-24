package com.dataFoot.ProjetData.repository;

import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatOffensiveDto;
import com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatPasseDto;
import com.dataFoot.ProjetData.model.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerStatRepository extends JpaRepository<PlayerStats, Long> {

    Optional<PlayerStats> findByPlayer_IdAndMatch_Id(Long joueurId, Long matchId);


    Optional<PlayerStats> findByPlayer_Id(Long joueurId);



    @Query("""
SELECT COUNT(ps)
FROM PlayerStats ps
WHERE ps.player.id = :playerId
AND ps.minutePlayed > 0
""")
    int countByPlayerIdMatchPlayed(@Param("playerId") int playerId);

    @Query("""
SELECT SUM(ps.minutePlayed)
FROM PlayerStats ps
WHERE ps.player.id = :playerId
""")
    int countByPlayerIdMinutesPlayed(@Param("playerId") int playerId);

    @Query("""
    SELECT new com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatOffensiveDto(
        ps.player.id,
        ps.player.firstName,
        ps.club.name,
        ps.club.id,
        ps.club.logo,
        COALESCE(SUM(ps.totalGoal), 0L),
        COALESCE(SUM(ps.totalShoot), 0L),
        COALESCE(SUM(ps.shootOnTarget), 0L)
    )
    FROM PlayerStats ps
    WHERE ps.match.league.id = :leagueId
    GROUP BY ps.player.id, ps.player.firstName, ps.club.name, ps.club.id, ps.club.logo
    ORDER BY COALESCE(SUM(ps.totalGoal), 0L) DESC,
             COALESCE(SUM(ps.totalShoot), 0L) DESC,
             COALESCE(SUM(ps.shootOnTarget), 0L) DESC
""")
    List<PlayerStatOffensiveDto> findPlayerStatsByLeagueId(@Param("leagueId") Long leagueId);
    @Query("""
    SELECT new com.dataFoot.ProjetData.dto.player.playerStat.PlayerStatPasseDto(
        ps.player.id,
        ps.player.firstName,
        ps.club.name,
        ps.club.id,
        ps.club.logo,
        COALESCE(SUM(ps.assist), 0L),
        COALESCE(SUM(ps.keyPasse), 0L),
        COALESCE(SUM(ps.totalPasse), 0L),
        COALESCE(SUM(ps.accuracyPass), 0L)
    )
    FROM PlayerStats ps
    WHERE ps.match.league.id = :leagueId
    GROUP BY ps.player.id, ps.player.firstName, ps.club.name, ps.club.id, ps.club.logo
    ORDER BY COALESCE(SUM(ps.assist), 0L) DESC,
             COALESCE(SUM(ps.keyPasse), 0L) DESC,
             COALESCE(SUM(ps.totalPasse), 0L) DESC,
             COALESCE(SUM(ps.accuracyPass), 0L)
""")
    List<PlayerStatPasseDto> findPlayerStatsPasseByLeagueId(@Param("leagueId") Long leagueId);

    @Query("""
       select coalesce(sum(ps.totalGoal), 0)
       from PlayerStats ps
       where ps.player.id = :playerId
       """)
    int sumTotalGoalByPlayerId(@Param("playerId") Long playerId);

    @Query("""
SELECT SUM(ps.totalGoal)
FROM PlayerStats ps
WHERE ps.player.id = :playerId
""")
    int countByPlayerIdTotalGoal(@Param("playerId") Long playerId);
}
