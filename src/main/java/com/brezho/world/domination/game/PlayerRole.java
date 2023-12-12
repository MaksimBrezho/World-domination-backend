package com.brezho.world.domination.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "player_roles")
public class PlayerRole {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EPlayerRole name;

    //Constructor
    public PlayerRole(EPlayerRole name) {
        this.name = name;
    }
}
