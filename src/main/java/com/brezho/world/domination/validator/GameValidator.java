package com.brezho.world.domination.validator;

import com.brezho.world.domination.payload.request.GameCreateRequest;
import com.brezho.world.domination.payload.request.TeamRequest;
import com.brezho.world.domination.payload.request.UnitRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameValidator {
    public static boolean validateGameCreateRequest(GameCreateRequest request) {
        return validateTeams(request.getTeams());
    }

    private static boolean validateTeams(List<TeamRequest> teams) {
        Set<String> teamNames = new HashSet<>();
        for (TeamRequest team : teams) {
            if (!teamNames.add(team.getTeamName())) {
                return false; // Найдено дублирующееся название команды
            }
            Set<String> unitNames = new HashSet<>();
            for (UnitRequest unit : team.getUnits()) {
                if (!unitNames.add(unit.getName())) {
                    return false; // Найдено дублирующееся название юнита
                }
            }
        }
        return true;
    }
}

