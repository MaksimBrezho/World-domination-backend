package com.brezho.world.domination.repository;

import java.util.List;
import java.util.Optional;

import com.brezho.world.domination.game.GameParameters;
import com.brezho.world.domination.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameParametersRepository extends JpaRepository<GameParameters, Long> {

    List<GameParameters> findByTitleContaining(String title);

    Optional<GameParameters> findById(Long paramId);

    Optional<GameParameters> findByTitle(String title);

    boolean existsByOwnerAndTitle(String owner, String title);

    Optional<GameParameters> findByOwnerAndTitle(String owner, String title);
}
