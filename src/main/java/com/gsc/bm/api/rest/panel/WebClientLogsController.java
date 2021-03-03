package com.gsc.bm.api.rest.panel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsc.bm.repo.external.GameLogRecord;
import com.gsc.bm.repo.external.WebClientLogRecord;
import com.gsc.bm.repo.external.WebClientLogRepository;
import com.gsc.bm.service.session.GameLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("rest/v1/logs/client/web")
public class WebClientLogsController {

    private final Environment env;
    private final WebClientLogRepository repo;

    @Autowired
    public WebClientLogsController(Environment env, WebClientLogRepository repo) {
        this.env = env;
        this.repo = repo;
    }

    // TODO authenticate this!!!
    @PostMapping(value = "/")
    public void saveLogs(@RequestBody JsonNode logObj) {
        if (Objects.equals(env.getProperty("log.db"), "N"))
            return;

        StringJoiner sessionInfo = new StringJoiner(",");
        Iterator<JsonNode> additional = logObj.at("/additional").elements();
        while (additional.hasNext())
            sessionInfo.add(additional.next().textValue());

        repo.save(new WebClientLogRecord(
                logObj.at("/timestamp").asText(),
                sessionInfo.toString(),
                logObj.at("/message").asText()
        ));
    }

    @GetMapping(value = "/qty", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getLogQty() {
        return repo.count();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/drain", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] drainLogs() throws JsonProcessingException {
        List<WebClientLogRecord> logs = new ArrayList<>((int) repo.count());
        Iterable<WebClientLogRecord> records = repo.findAll();
        records.forEach(logs::add);
        return new ObjectMapper().writeValueAsBytes(logs);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete")
    public boolean cleanLogs() {
        Iterable<WebClientLogRecord> records = repo.findAll();
        repo.deleteAll(records);
        return true;
    }
}
