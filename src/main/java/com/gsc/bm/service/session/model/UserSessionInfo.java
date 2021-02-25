package com.gsc.bm.service.session.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserSessionInfo {

    private final String userLogin;
    private final String sessionId;
    @Setter
    private String activity;

}
