package com.brezho.world.domination.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NegHostAnswRequest {
    private Long senderTeamId;
    private Long recipientTeamId;
    private String status;
}
