package com.gsc.bm.repo.external;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameLogRepository extends CrudRepository<GameLogRecord, String> {

    List<GameLogRecord> findAllByStatus(String status);

}
