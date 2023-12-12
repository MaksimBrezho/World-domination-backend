package com.brezho.world.domination.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "round_status")
public class RoundStatus {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERoundStatus name;

    //Constructor
    public RoundStatus(ERoundStatus name) {
        this.name = name;
    }

}
