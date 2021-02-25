package com.gsc.bm.service.account.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class UserAccountInfo {
    String username;
    String email;
    String role;
    // TODO this is becoming gigantic to load all at once
    Set<UserGuiDeck> decks;
    UserCollection collection;
}
