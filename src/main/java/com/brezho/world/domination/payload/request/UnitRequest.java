package com.brezho.world.domination.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequest {
    @NotBlank
    private String name;

    @Min(value = 0)
    private double devLevel;
}
