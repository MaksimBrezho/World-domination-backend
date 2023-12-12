package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.*;
import com.brezho.world.domination.models.ERole;
import com.brezho.world.domination.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NegotiationStatusRepository extends JpaRepository<NegotiationStatus, Long> {
    Optional<NegotiationStatus> findByName(ENegotiationStatus name);
}
