package com.gsc.bm.repo.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StarterDeckRepository extends JpaRepository<StarterDeckRecord, StarterDeckRecord> {

    List<StarterDeckRecord> findAllByPgClazz(String pgClazz);
}
