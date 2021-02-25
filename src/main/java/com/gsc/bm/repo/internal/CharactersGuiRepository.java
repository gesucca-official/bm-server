package com.gsc.bm.repo.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharactersGuiRepository extends JpaRepository<CharactersGuiRecord, String> {
}
