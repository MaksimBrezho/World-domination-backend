package com.brezho.world.domination.repository;

import java.util.Optional;

import com.brezho.world.domination.game.ERoundStatus;
import com.brezho.world.domination.game.RoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundStatusRepository extends JpaRepository<RoundStatus, Long> {
    Optional<RoundStatus> findByName(ERoundStatus name);
}