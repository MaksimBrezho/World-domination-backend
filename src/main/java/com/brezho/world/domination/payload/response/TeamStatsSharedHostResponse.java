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
public class TeamStatsSharedHostResponse {
    private Long id;
    private String teamName;
    private List<UnitResponse> units;
    private boolean nucTech;
    private int numBombs;
    private int budget;
    private double devLevel;
}
