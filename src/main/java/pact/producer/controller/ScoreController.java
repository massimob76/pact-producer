package pact.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pact.producer.handler.ScoreHandler;
import pact.producer.model.Score;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("/api/v1/scores")
public class ScoreController {

    @Autowired
    private ScoreHandler scoreHandler;

    @GetMapping
    List<Score> getAllScores() {
        return scoreHandler.getAllScores();
    }

    @GetMapping(path = "/{name}")
    Score getScore(@PathVariable ("name") String name) {
        return scoreHandler.getScore(name);
    }

    @PostMapping
    @ResponseStatus(ACCEPTED)
    void createScore(@RequestBody Score score) {
        scoreHandler.createScore(score);

    }

    @PutMapping("/{name}")
    @ResponseStatus(ACCEPTED)
    void updateScore(@PathVariable ("name") String name, @RequestBody Score score) {
        scoreHandler.updateScore(name, score);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(ACCEPTED)
    void deleteScore(@PathVariable ("name") String name) {
        scoreHandler.deleteScore(name);
    }
}
