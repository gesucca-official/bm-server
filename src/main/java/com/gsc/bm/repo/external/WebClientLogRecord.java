package com.gsc.bm.repo.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOG002_WEB_CLIENT")

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WebClientLogRecord {

    @Id
    @Column(name = "C_TIMESTAMP")
    private String timestamp;

    @Column(name = "T_ADDITIONAL")
    private String additional;

    @Column(name = "T_MESSAGE")
    private String message;
}
