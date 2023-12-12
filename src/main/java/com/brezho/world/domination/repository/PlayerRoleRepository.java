package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.EPlayerRole;
import com.brezho.world.domination.game.PlayerRole;
import com.brezho.world.domination.models.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRoleRepository extends JpaRepository<PlayerRole, Long> {
    Optional<PlayerRole> findByName(EPlayerRole eRole);
}
