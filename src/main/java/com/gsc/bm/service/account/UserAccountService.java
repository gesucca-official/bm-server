package com.gsc.bm.service.account;

import com.gsc.bm.service.account.model.UserAccountInfo;
import com.gsc.bm.service.account.model.UserGuiDeck;

public interface UserAccountService {

    UserAccountInfo loadUserAccountInfo(String username);

    void addUserDeck(String username, UserGuiDeck deck);

    void deleteUserDeck(String username, UserGuiDeck deck);

}
