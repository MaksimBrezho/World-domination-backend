package com.brezho.world.domination.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NegForHostResponse {
    private Long senderNameId;
    private String senderTeamName;
    private Long recipientTeamId;
    private String recipientTeamName;
    private String status;
}
