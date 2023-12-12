package com.brezho.world.domination.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameParametersRequest {
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 3, max = 40, message = "Title must be between 3 and 40 characters")
    private String title;

    private String owner;

    @Min(value = 1, message = "Number of rounds must be greater than or equal to 1")
    private int numRounds;

    @DecimalMin(value = "0", inclusive = true, message = "Initial ecology level must be greater than or equal to 0")
    @DecimalMax(value = "1", inclusive = true, message = "Initial ecology level must be less than or equal to 1")
    private double initialEcologyLevel;

    @Min(value = 1, message = "Initial capital must be greater than or equal to 1")
    private int initialCapital;

    @Min(value = 1, message = "Net city income must be greater than or equal to 1")
    private int netCityIncome;

    @NotBlank(message = "Income changes cannot be blank")
    @Size(min = 1, max = 100, message = "Income changes must be between 1 and 100 characters")
    private String incomeChanges;

    @Min(value = 1, message = "Development cost must be greater than or equal to 1")
    private int developmentCost;

    @Min(value = 1, message = "Shield cost must be greater than or equal to 1")
    private int shieldCost;

    @Min(value = 1, message = "Nuclear tech cost must be greater than or equal to 1")
    private int nuclearTechCost;

    @Min(value = 1, message = "Eco program cost must be greater than or equal to 1")
    private int ecoProgramCost;

    @Min(value = 1, message = "Bomb cost must be greater than or equal to 1")
    private int bombCost;

    @Min(value = 0, message = "Number of bombs produced must be greater than or equal to 0")
    private int numBombsProduced;

    @Min(value = 0, message = "Bomb storage limit must be greater than or equal to 0")
    private int bombStorageLimit;

    @DecimalMin(value = "0", message = "Eco program impact on ecology cannot be less than 0")
    @DecimalMax(value = "1", message = "Eco program impact on ecology cannot be greater than 1")
    private double ecoProgramImpactOnEcology;

    @DecimalMin(value = "0", message = "Nuclear tech impact on ecology cannot be less than 0")
    @DecimalMax(value = "1", message = "Nuclear tech impact on ecology cannot be greater than 1")
    private double nuclearTechImpactOnEcology;

    @DecimalMin(value = "0", message = "Bomb construction impact on ecology cannot be less than 0")
    @DecimalMax(value = "1", message = "Bomb construction impact on ecology cannot be greater than 1")
    private double bombConstructionImpactOnEcology;

    @DecimalMin(value = "0", message = "Bomb drop impact on ecology cannot be less than 0")
    @DecimalMax(value = "1", message = "Bomb drop impact on ecology cannot be greater than 1")
    private double bombDropImpactOnEcology;

    @Min(value = 0, message = "Number of accepted teams sent must be greater than or equal to 0")
    private int numAcceptedSent;

    @Min(value = 0, message = "Number of requests sent must be greater than or equal to 0")
    private int numRequestsSent;

    @DecimalMin(value = "0", message = "Development change cannot be less than 0")
    @DecimalMax(value = "1", message = "Development change cannot be greater than 1")
    private double developmentChange;

    @Min(value = 2, message = "Number of units must be greater than or equal to 2")
    private int numUnits;
}
