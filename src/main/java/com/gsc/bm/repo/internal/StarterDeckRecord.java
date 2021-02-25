package com.gsc.bm.repo.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "DCK001_STARTERS")
@IdClass(StarterDeckRecord.class)

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class StarterDeckRecord implements Serializable {

    @Id
    @Column(name = "C_PG_REF_CLASS")
    private String pgClazz;

    @Id
    @Column(name = "C_CRD_REF_CLASS")
    private String cardClazz;

    @Column(name = "N_QTY")
    private int qty;
}
