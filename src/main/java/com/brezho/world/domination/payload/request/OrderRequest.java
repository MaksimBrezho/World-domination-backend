package com.brezho.world.domination.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private List<Long> unitsDevIds;
    private List<Long> unitsShieldIds;
    private boolean ecoProgram;
    private List<Long> sanctionsIds;
    private boolean nucTechDev;
    private int bombDev;
    private List<Long> dropBombsIds;
}
