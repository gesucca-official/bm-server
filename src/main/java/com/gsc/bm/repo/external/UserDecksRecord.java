package com.gsc.bm.repo.external;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Table(name = "USR002_DECKS")
@IdClass(UserDeckRecordKey.class)

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDecksRecord {

    @Id
    @Column(name = "C_USERNAME")
    private String username;

    @Column(name = "C_DECK_ID")
    private String deckId;

    @Type(type = "jsonb")
    @Column(name = "J_DECK", columnDefinition = "json")
    private UserStoredDeck deck;

    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class UserStoredDeck {
        String characterClazz;
        String basicActionCardClazz;
        String lastResortCardClazz;
        List<String> characterBoundCardsClazz;
        List<String> regularCardsClazz;
    }
}
