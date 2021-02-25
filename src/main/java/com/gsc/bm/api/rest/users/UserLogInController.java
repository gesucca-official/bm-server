package com.gsc.bm.api.rest.users;

import com.gsc.bm.service.session.AuthService;
import com.gsc.bm.service.session.ConnectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("rest/v1/sign-in")
public class UserLogInController {

    private final AuthService authService;
    private final ConnectionsService connectionsService;

    @Autowired
    public UserLogInController(AuthService authService, ConnectionsService connectionsService) {
        this.authService = authService;
        this.connectionsService = connectionsService;
    }

    @PostMapping(value = "/validate")
    public boolean validateCredentials(@RequestBody Map<String, String> credentials) {
        if (connectionsService.getAllConnectedUsers().contains(credentials.get("username")))
            return false; // rough patch to avoid multiple connections with the same user
        try {
            authService.getAuthTokenOrFail(credentials.get("username"), credentials.get("password"));
        } catch (AuthenticationException e) {
            return false;
        }
        return true;
    }
}
