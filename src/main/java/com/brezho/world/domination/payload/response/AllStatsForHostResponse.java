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
public class AllStatsForHostResponse {
    private List<TeamStatsSharedHostResponse> teams;
    private double ecoLevel;
    private int roundNum;
}
