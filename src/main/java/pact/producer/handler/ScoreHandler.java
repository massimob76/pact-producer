package pact.producer.handler;

import pact.producer.dto.ScoreUsernameTimestamp;

import java.util.List;
import pact.producer.exception.DuplicatedScoreException;
import pact.producer.exception.UserNotFoundException;

public class ScoreHandler {

    public List<ScoreUsernameTimestamp> getAllScores() {
        return null;
    }

    public ScoreUsernameTimestamp getScore(String name) throws UserNotFoundException {
        return null;
    }

    public void createScore(String name, int score) throws DuplicatedScoreException {

    }

    public void updateScore(String name, int score) throws UserNotFoundException {

    }

    public void deleteScore(String name) throws UserNotFoundException {

    }
}
