package com.brezho.world.domination.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private Long id;
    private String title;
    private int numTeams;
}
