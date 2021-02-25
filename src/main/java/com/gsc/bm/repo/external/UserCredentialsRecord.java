package com.gsc.bm.repo.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USR001_CREDENTIALS")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UserCredentialsRecord {

    @Id
    @Column(name = "C_USERNAME")
    private String username;

    @Column(name = "T_EMAIL")
    private String email;

    @Column(name = "T_SALTED_HASH")
    private String saltedHash;

    @Column(name = "T_ROLE")
    private String role;
}
