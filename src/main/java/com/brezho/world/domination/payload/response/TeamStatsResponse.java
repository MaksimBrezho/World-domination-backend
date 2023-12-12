package com.brezho.world.domination.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatsResponse {
    private Long id;
    private String teamName;
    private List<UnitResponse> units;
    private boolean nucTech;
    private int numBombs;
    //private double ecoLevel;
    private int budget;
    private List<String> sanctions;
    //private List<String> teams;
    //private int roundNum;
    private AllStatsForTeamsResponse allStatsForTeams;
}

