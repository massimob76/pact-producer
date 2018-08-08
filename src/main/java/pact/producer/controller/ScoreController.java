package pact.producer.controller;

import static org.springframework.http.HttpStatus.ACCEPTED;

import java.util.Collection;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pact.producer.dto.ScoreUsername;
import pact.producer.dto.ScoreUsernameTimestamp;
import pact.producer.exception.DuplicatedScoreException;
import pact.producer.exception.UserNotFoundException;
import pact.producer.handler.ScoreHandler;

@RestController
@RequestMapping("/api/v1/scores")
public class ScoreController {

    @Autowired
    private ScoreHandler scoreHandler;

    @GetMapping
    Collection<ScoreUsernameTimestamp> getAllScores() {
        return scoreHandler.getAllScores();
    }

    @GetMapping(path = "/{name}")
    ScoreUsernameTimestamp getScore(@PathVariable ("name") String name) throws UserNotFoundException {
        return scoreHandler.getScore(name);
    }

    @PostMapping
    @ResponseStatus(ACCEPTED)
    void createScore(@Valid @RequestBody ScoreUsername scoreUsername) throws DuplicatedScoreException {
        scoreHandler.createScore(scoreUsername.getName(), scoreUsername.getScore());

    }

    @PutMapping("/{name}")
    @ResponseStatus(ACCEPTED)
    void updateScore(@PathVariable ("name") String name, @RequestBody int score) throws UserNotFoundException {
        scoreHandler.updateScore(name, score);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(ACCEPTED)
    void deleteScore(@PathVariable ("name") String name) throws UserNotFoundException {
        scoreHandler.deleteScore(name);
    }
}
