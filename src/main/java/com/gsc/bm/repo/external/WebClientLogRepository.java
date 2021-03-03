package com.gsc.bm.repo.external;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebClientLogRepository extends CrudRepository<WebClientLogRecord, String> {
}
