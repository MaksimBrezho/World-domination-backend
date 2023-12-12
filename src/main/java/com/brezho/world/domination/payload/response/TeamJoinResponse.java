package com.brezho.world.domination.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamJoinResponse {
    private Long id;
    private String name;
    private String captain;
    private List<String> players;
}
