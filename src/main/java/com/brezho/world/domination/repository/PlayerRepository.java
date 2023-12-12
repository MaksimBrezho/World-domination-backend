package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.Player;
import com.brezho.world.domination.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    void deleteByUser(User user);
}
