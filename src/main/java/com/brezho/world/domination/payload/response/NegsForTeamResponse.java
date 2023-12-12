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
public class NegsForTeamResponse {
    private List<NegForTeamResponse> negs;
    private List<TeamInfoResponse> teams; //все команды, кроме команды игрока
}
