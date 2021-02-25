package com.gsc.bm.repo.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CRD002_KEYWORDS")

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class CardKeywordsRecord {

    @Id
    @Column(name = "C_KEYWORD")
    private String keyword;

    @Column(name = "T_HTML")
    private String html;
}
