package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Iterable<Game> findByRoundNum(int i);
}