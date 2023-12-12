package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByGameId(Long gameId);

    List<Team> findAllByIdNot(Long teamId);
}
