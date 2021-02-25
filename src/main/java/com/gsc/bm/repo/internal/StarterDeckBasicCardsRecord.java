package com.gsc.bm.repo.internal;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DCK002_STARTERS_CH")

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class StarterDeckBasicCardsRecord {

    @Id
    @Column(name = "C_PG_REF_CLASS")
    private String pgClazz;

    @Column(name = "T_BASIC_REF_CLASS")
    private String basicClazz;

    @Column(name = "T_CH_BOUND_1_REF_CLASS")
    private String chBoundClazz1;

    @Column(name = "T_CH_BOUND_2_REF_CLASS")
    private String chBoundClazz2;

    @Column(name = "T_LAST_RESORT_REF_CLASS")
    private String lastResortClazz;
}
