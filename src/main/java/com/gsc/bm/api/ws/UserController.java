package com.gsc.bm.api.ws;

import com.gsc.bm.service.account.UserAccountService;
import com.gsc.bm.service.account.model.UserAccountInfo;
import com.gsc.bm.service.account.model.UserGuiDeck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    private final UserAccountService service;

    @Autowired
    public UserController(UserAccountService service) {
        this.service = service;
    }

    @MessageMapping("/user/{username}/account")
    @SendToUser("/account/data")
    public UserAccountInfo getUserAccountInfo(@DestinationVariable String username) {
        return service.loadUserAccountInfo(username);
    }

    @MessageMapping("/user/{username}/deck")
    @SendToUser("/account/data")
    public UserAccountInfo addUserDeck(@DestinationVariable String username, @Payload UserGuiDeck deck) {
        service.addUserDeck(username, deck);
        return service.loadUserAccountInfo(username);
    }

    @MessageMapping("/user/{username}/deck/delete")
    @SendToUser("/account/data")
    public UserAccountInfo deleteUserDeck(@DestinationVariable String username, @Payload UserGuiDeck deck) {
        service.deleteUserDeck(username, deck);
        return service.loadUserAccountInfo(username);
    }
}
