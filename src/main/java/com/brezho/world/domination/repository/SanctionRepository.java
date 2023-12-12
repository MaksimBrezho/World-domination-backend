package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.Sanction;
import com.brezho.world.domination.game.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long> {
    List<Sanction> findByTargetTeamAndRoundNumber(Team targetTeam, int roundNumber);

    //List<Sanction> findByTargetTeam(Long id);
    //List<String> findDistinctIssuingTeamNameByTargetTeamAndRoundNumber(Team targetTeam, int roundNumber);
}

