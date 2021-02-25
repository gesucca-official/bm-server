package com.gsc.bm.service.session.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.SerializationUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
public class ActionLog {

    private final String when = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    private final String what;
    private final Object data;

    public ActionLog(String what, Object data) {
        this.what = what;
        this.data = SerializationUtils.deserialize(SerializationUtils.serialize(data)); // is this really necessary?
    }
}