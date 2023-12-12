package com.brezho.world.domination.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "negotiations")
public class Negotiation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "round_num")
    private int roundNumber;

    @ManyToOne
    @JoinColumn(name = "sender_team", referencedColumnName = "id")
    private Team senderTeam;

    @ManyToOne
    @JoinColumn(name = "recipient_team", referencedColumnName = "id")
    private Team recipientTeam;

    @ManyToOne
    @JoinColumn(name = "neg_status", referencedColumnName = "id")
    private NegotiationStatus negStatus;

    public Negotiation(int roundNumber, Team senderTeam, Team recipientTeam, NegotiationStatus negStatus) {
        this.roundNumber = roundNumber;
        this.senderTeam = senderTeam;
        this.recipientTeam = recipientTeam;
        this.negStatus = negStatus;
    }

}
