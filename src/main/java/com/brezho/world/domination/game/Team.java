package com.brezho.world.domination.game;

//import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "teams")
public class Team {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "players_num")
    private int numberOfPlayers;

    @OneToMany
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private List<Player> players;

    @Column(name = "team_name")
    @NotBlank(message = "The team name must not be empty")
    private String teamName;

    @OneToMany
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private List<Unit> units;

    @Column(name = "budget")
    private int budget;

    @Column(name = "bombs_num")
    private int numberOfBombs;

    @Column(name = "bombs_dev_num")
    private int numberOfBombsInDev;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;

    @Column(name = "nuc_tech", columnDefinition = "boolean default false")
    private boolean nuclearTech;

    @Column(name = "nuc_tech_dev", columnDefinition = "boolean default false")
    private boolean nuclearTechDev;

    @ManyToOne
    @JoinColumn(name = "team_status_id", referencedColumnName = "id")
    private TeamStatus teamStatus;

    @Column(name = "isOrder", columnDefinition = "boolean default false")
    private boolean isOrder;

    // Constructor
    public Team(int numberOfPlayers,
                List<Player> players,
                String teamName,
                List<Unit> units,
                int budget,
                int numberOfBombs,
                int numberOfBombsInDev,
                Game game,
                boolean nuclearTech,
                boolean nuclearTechDev,
                TeamStatus teamStatus,
                boolean isOrder) {
        this.numberOfPlayers = numberOfPlayers;
        this.players = players;
        this.teamName = teamName;
        this.units = units;
        this.budget = budget;
        this.numberOfBombs = numberOfBombs;
        this.numberOfBombsInDev = numberOfBombsInDev;
        this.game = game;
        this.nuclearTech = nuclearTech;
        this.nuclearTechDev = nuclearTechDev;
        this.teamStatus = teamStatus;
        this.isOrder = isOrder;
    }

    public double getDevLevel() {
        if (units == null || units.isEmpty()) {
            return 0.0; // Return 0 if no units are present
        }

        double totalDevLevel = 0.0;
        for (Unit unit : units) {
            totalDevLevel += unit.getLevelOfDevelopment();
        }

        return totalDevLevel / units.size();
    }

    public void addBombs(int count) {
        numberOfBombs += count;
    }

    public void addBombsInDev(int count) {
        numberOfBombsInDev += count;
    }

    public void removeBombs(int count) {
        numberOfBombs -= count;
    }

    public void updateNumberOfBombs() {
        numberOfBombs += numberOfBombsInDev;
        numberOfBombsInDev = 0;
    }

    public void addBudget(int amount) {
        budget += amount;
    }

    public void removeBudget(int amount) {
        budget -= amount;
    }

    public void beginNuclearTechDev()
    {
        nuclearTechDev = true;
    }

    public void updateNuclearTech() {
        if (nuclearTechDev) {
            nuclearTech = true;
        }
        nuclearTechDev = false;
    }

    public double getDevLevelSum() {
        double devLevel = 0;
        for (Unit unit: units) {
            devLevel += unit.getLevelOfDevelopment();
        }
        return devLevel;
    }

}
