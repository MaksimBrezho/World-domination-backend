package com.brezho.world.domination.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateDefaultResponse {
    private int numTeams = 6;
    private int numUnits = 4;
    private Long paramsId = 1L;
}
