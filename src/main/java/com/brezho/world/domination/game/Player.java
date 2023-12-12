package com.brezho.world.domination.game;

import javax.persistence.*;
import com.brezho.world.domination.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "players")
public class Player {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "player_role_id", referencedColumnName = "id")
    private PlayerRole playerRole;

    // Constructor
    public Player(User user, PlayerRole playerRole) {
        this.user = user;
        this.playerRole = playerRole;
    }
}

