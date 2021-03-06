package com.gsc.bm.api.rest.panel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsc.bm.repo.external.GameLogRecord;
import com.gsc.bm.repo.external.GameLogRepository;
import com.gsc.bm.service.session.GameLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("rest/v1/logs/back/games")
public class GameLogsController {

    private final GameLogRepository gameLogRepo;

    @Autowired
    public GameLogsController(GameLogRepository gameLogRepo) {
        this.gameLogRepo = gameLogRepo;
    }

    @GetMapping(value = "/qty", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getLoggedGamesQty() {
        return gameLogRepo.count();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/open", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GameLogRecord> getOpenedGames() {
        List<GameLogRecord> openGames = new ArrayList<>();
        openGames.addAll(gameLogRepo.findAllByStatus(GameLoggingService.STARTED));
        openGames.addAll(gameLogRepo.findAllByStatus(GameLoggingService.IN_PROGRESS));
        return openGames;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/drain", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] drainLogs() throws JsonProcessingException {
        List<GameLogRecord> logs = new ArrayList<>((int) gameLogRepo.count());
        Iterable<GameLogRecord> records = gameLogRepo.findAll();
        records.forEach(logs::add);
        return new ObjectMapper().writeValueAsBytes(logs);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete")
    public boolean cleanLogs() {
        Iterable<GameLogRecord> records = gameLogRepo.findAll();
        gameLogRepo.deleteAll(records);
        return true;
    }

}
