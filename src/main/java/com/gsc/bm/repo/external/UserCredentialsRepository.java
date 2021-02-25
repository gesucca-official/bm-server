package com.gsc.bm.repo.external;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserCredentialsRepository extends CrudRepository<UserCredentialsRecord, String> {
    List<UserCredentialsRecord> findAllByEmail(String email);
}
