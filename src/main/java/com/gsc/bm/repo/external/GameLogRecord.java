package com.gsc.bm.repo.external;

import com.gsc.bm.service.session.model.ActionLog;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Table(name = "LOG001_GAMES")

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameLogRecord {

    @Id
    @Column(name = "C_GAME_ID")
    private String gameId;

    @Column(name = "T_PLAYERS")
    private String players;

    @Column(name = "T_TYPE")
    private String type;

    @Column(name = "T_DATE")
    private String date;

    @Column(name = "T_STATUS")
    private String status;

    @Type(type = "jsonb")
    @Column(name = "J_FULL_LOG", columnDefinition = "json")
    private List<ActionLog> fullLog;

}
