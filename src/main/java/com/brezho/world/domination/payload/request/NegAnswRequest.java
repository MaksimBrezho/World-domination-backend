package com.brezho.world.domination.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegAnswRequest {
    private Long recipientTeamId;
    private String status;
}
