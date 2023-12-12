package com.brezho.world.domination.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "game_params")
public class GameParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "owner")
    private String owner;

    @Column(name = "num_rounds")
    @Min(value = 1, message = "Number of rounds must be greater than or equal to 1")
    private int numRounds;

    @Column(name = "initial_ecology_level")
    @DecimalMin(value = "0", inclusive = true, message = "Initial ecology level must be greater than or equal to 0")
    @DecimalMax(value = "1", inclusive = true, message = "Initial ecology level must be less than or equal to 1")
    private double initialEcologyLevel;

    @Column(name = "initial_capital")
    @Min(value = 1, message = "Initial capital must be greater than or equal to 1")
    private int initialBudget;

    @Column(name = "net_city_income")
    @Min(value = 1, message = "Net city income must be greater than or equal to 1")
    private int netCityIncome;

    @Column(name = "income_changes")
    private String incomeChanges;

    @Column(name = "development_cost")
    @Min(value = 1, message = "Development cost must be greater than or equal to 1")
    private int developmentCost;

    @Column(name = "shield_cost")
    @Min(value = 1, message = "Shield cost must be greater than or equal to 1")
    private int shieldCost;

    @Column(name = "nuclear_tech_cost")
    @Min(value = 1, message = "Nuclear tech cost must be greater than or equal to 1")
    private int nuclearTechCost;

    @Column(name = "eco_program_cost")
    @Min(value = 1, message = "Eco program cost must be greater than or equal to 1")
    private int ecoProgramCost;

    @Column(name = "bomb_cost")
    @Min(value = 1, message = "Bomb cost must be greater than or equal to 1")
    private int bombCost;

    @Column(name = "num_bombs_produced")
    @Min(value = 0, message = "Number of bombs produced must be greater than or equal to 0")
    private int numBombsProduced;

    @Column(name = "bomb_storage_limit")
    @Min(value = 0, message = "Bomb storage limit must be greater than or equal to 0")
    private int bombStorageLimit;

    @Column(name = "eco_program_impact_on_ecology")
    private double ecoProgramImpactOnEcology;

    @Column(name = "nuclear_tech_impact_on_ecology")
    private double nuclearTechImpactOnEcology;

    @Column(name = "bomb_construction_impact_on_ecology")
    private double bombConstructionImpactOnEcology;

    @Column(name = "bomb_drop_impact_on_ecology")
    private double bombDropImpactOnEcology;

    @Column(name = "num_accepted_sent")
    @Min(value = 0, message = "Number of accepted countries sent must be greater than or equal to 0")
    private int numAcceptedSent;

    @Column(name = "num_requests_sent")
    @Min(value = 0, message = "Number of requests sent must be greater than or equal to 0")
    private int numRequestsSent;

    @Column(name = "development_change")
    private double developmentChange;

    @Column(name = "num_units")
    @Min(value = 2, message = "Number of units must be greater than or equal to 2")
    private int numUnits;

    //Constructor
    public GameParameters(String title, String owner, int numRounds, double initialEcologyLevel,
                          int initialBudget, int netCityIncome, String incomeChanges,
                          int developmentCost, int shieldCost, int nuclearTechCost,
                          int ecoProgramCost, int bombCost, int numBombsProduced,
                          int bombStorageLimit, double ecoProgramImpactOnEcology,
                          double nuclearTechImpactOnEcology, double bombConstructionImpactOnEcology,
                          double bombDropImpactOnEcology, int numAcceptedSent, int numRequestsSent,
                          double developmentChange, int numUnits) {
        this.title = title;
        this.owner = owner;
        this.numRounds = numRounds;
        this.initialEcologyLevel = initialEcologyLevel;
        this.initialBudget = initialBudget;
        this.netCityIncome = netCityIncome;
        this.incomeChanges = incomeChanges;
        this.developmentCost = developmentCost;
        this.shieldCost = shieldCost;
        this.nuclearTechCost = nuclearTechCost;
        this.ecoProgramCost = ecoProgramCost;
        this.bombCost = bombCost;
        this.numBombsProduced = numBombsProduced;
        this.bombStorageLimit = bombStorageLimit;
        this.ecoProgramImpactOnEcology = ecoProgramImpactOnEcology;
        this.nuclearTechImpactOnEcology = nuclearTechImpactOnEcology;
        this.bombConstructionImpactOnEcology = bombConstructionImpactOnEcology;
        this.bombDropImpactOnEcology = bombDropImpactOnEcology;
        this.numAcceptedSent = numAcceptedSent;
        this.numRequestsSent = numRequestsSent;
        this.developmentChange = developmentChange;
        this.numUnits = numUnits;
    }

}

