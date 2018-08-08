package pact.producer.handler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pact.producer.dto.ScoreUsernameTimestamp;
import pact.producer.exception.DuplicatedScoreException;
import pact.producer.exception.UserNotFoundException;
import pact.producer.service.TimeProvider;

@Component
public class ScoreHandler {

    private Map<String, ScoreUsernameTimestamp> scoreMap = new ConcurrentHashMap<>();

    @Autowired
    private TimeProvider timeProvider;

    public Collection<ScoreUsernameTimestamp> getAllScores() {
        return scoreMap.values();
    }

    public ScoreUsernameTimestamp getScore(String name) throws UserNotFoundException {
        ScoreUsernameTimestamp scoreUsernameTimestamp = scoreMap.get(name);
        if (scoreUsernameTimestamp == null) {
            throw new UserNotFoundException("Could not find username: " + name);
        }
        return scoreUsernameTimestamp;
    }

    public void createScore(String name, int score) throws DuplicatedScoreException {
        ScoreUsernameTimestamp scoreUsernameTimestamp = new ScoreUsernameTimestamp(name, score, timeProvider.now());
        boolean added = scoreMap.putIfAbsent(name, scoreUsernameTimestamp) == null;
        if (!added) {
            throw new DuplicatedScoreException("Username " + name + " already exists");
        }

    }

    public void updateScore(String name, int score) throws UserNotFoundException {
        BiFunction<String, ScoreUsernameTimestamp, ScoreUsernameTimestamp> remappingFunction = (s, scoreUsernameTimestamp) -> new ScoreUsernameTimestamp(name, score, timeProvider.now());
        boolean added = scoreMap.computeIfPresent(name, remappingFunction) != null;
        if (!added) {
            throw new UserNotFoundException("Could not find username: " + name);
        }
    }

    public void deleteScore(String name) throws UserNotFoundException {
        boolean removed = scoreMap.remove(name) != null;
        if (!removed) {
            throw new UserNotFoundException("Could not find username: " + name);
        }
    }
}
