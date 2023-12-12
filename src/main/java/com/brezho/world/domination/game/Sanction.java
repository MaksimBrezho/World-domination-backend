package com.brezho.world.domination.game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sanctions")
public class Sanction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "round_num")
    private int roundNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issuing_team", referencedColumnName = "id")
    private Team issuingTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_team", referencedColumnName = "id")
    private Team targetTeam;

    //Constructor
    public Sanction(int roundNumber,
                    Team issuingTeam,
                    Team targetTeam) {
        this.roundNumber = roundNumber;
        this.issuingTeam = issuingTeam;
        this.targetTeam = targetTeam;
    }
}
