package com.brezho.world.domination.repository;

import com.brezho.world.domination.game.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NegotiationRepository extends JpaRepository<Negotiation, Long> {
    //findByRecipientTeamAndNegStatus
    List<Negotiation> findByRecipientTeamAndNegStatus(Team recipientTeam, NegotiationStatus negStatus);
    List<Negotiation> findBySenderTeamAndRoundNumber(Team senderTeam, int roundNumber);

    Optional<Negotiation> findNegotiationByRoundNumberAndSenderTeamAndRecipientTeam(int roundNumber, Team senderTeam, Team recipientTeam);

    List<Negotiation> findByRoundNumberAndRecipientTeam(int roundNumber, Team playerTeam);

    List<Negotiation> findByRecipientTeam(Team team);

    //Optional<Object> findNegotiationByRoundNumberAndSenderTeam(int , Team );
}
