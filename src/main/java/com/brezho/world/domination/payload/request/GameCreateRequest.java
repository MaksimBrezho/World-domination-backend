package com.brezho.world.domination.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 40, message = "Title must be between 5 and 40 characters")
    private String title;

    @NotBlank(message = "Game code is required")
    @Size(min = 5, max = 20, message = "Game code must be between 5 and 20 characters")
    private String gameCode;

    @NotBlank(message = "Host is required")
    private String host;

    @NotNull(message = "Game parameters is required")
    private Long paramsId;

    @Min(value = 4, message = "Number of teams must be at least 4")
    private int numTeams;

    private List<TeamRequest> teams;
}

