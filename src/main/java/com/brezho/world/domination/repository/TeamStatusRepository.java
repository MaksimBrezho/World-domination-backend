package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.ETeamStatus;
import com.brezho.world.domination.game.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamStatusRepository extends JpaRepository<TeamStatus, Long> {
    Optional<TeamStatus> findByName(ETeamStatus name);
}
