package com.brezho.world.domination.game;


import com.brezho.world.domination.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @Column(name = "game_code")
    @Size(min = 6, max = 12)
    private String gameCode;

    @Column(name = "title")
    private String title;

    @OneToOne
    @JoinColumn(name = "host_id", referencedColumnName = "id")
    private User host;

    @Column(name = "num_teams")
    @Min(value = 1)
    private int numTeams;

    @JsonIgnore
    @OneToMany(mappedBy = "game")
    private List<Team> teams;

    @Column(name = "round_num")
    private int roundNum;

    @ManyToOne
    @JoinColumn(name = "round_status", referencedColumnName = "id")
    private RoundStatus roundStatus;

    @ManyToOne
    @JoinColumn(name = "param_id", referencedColumnName = "id")
    private GameParameters param;

    @Column(name = "eco_level_current_round")
    private double ecoLevelCurrentRound;

    @Column(name = "eco_level_next_round")
    private double ecoLevelNextRound;

    // Constructor
    public Game(String gameCode,
                String title,
                User host,
                int numTeams,
                List<Team> teams,
                int roundNum,
                RoundStatus roundStatus,
                GameParameters param,
                double ecoLevelCurrentRound,
                double ecoLevelNextRound) {
        this.gameCode = gameCode;
        this.title = title;
        this.host = host;
        this.numTeams = numTeams;
        this.teams = teams;
        this.roundNum = roundNum;
        this.roundStatus = roundStatus;
        this.param = param;
        this.ecoLevelCurrentRound = ecoLevelCurrentRound;
        this.ecoLevelNextRound = ecoLevelNextRound;
    }

    // Methods
}

