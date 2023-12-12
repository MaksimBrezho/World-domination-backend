package com.brezho.world.domination.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinGameRequest {
    @NotNull
    private Long teamId;

    @NotBlank()
    private String playerRole;
}
