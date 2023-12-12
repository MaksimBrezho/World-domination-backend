package com.brezho.world.domination.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "negotiation_statuses")
public class NegotiationStatus {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ENegotiationStatus name;

    public NegotiationStatus(ENegotiationStatus name) {
        this.name = name;
    }

}
