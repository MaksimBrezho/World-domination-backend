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
public class TeamStatsSharedResponse {
    private Long id;
    private String teamName;
    private List<UnitSharedResponse> units;
    private double devLevel;
}
