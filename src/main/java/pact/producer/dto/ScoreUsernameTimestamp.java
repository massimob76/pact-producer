package pact.producer.dto;

import java.time.Instant;

public class ScoreUsernameTimestamp {

    private final String name;
    private final int score;
    private final Instant timestamp;

    public ScoreUsernameTimestamp(String name, int score, Instant timestamp) {
        this.name = name;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreUsernameTimestamp score = (ScoreUsernameTimestamp) o;

        if (this.score != score.score) return false;
        if (name != null ? !name.equals(score.name) : score.name != null) return false;
        return timestamp != null ? timestamp.equals(score.timestamp) : score.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + score;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", timestamp=" + timestamp +
                '}';
    }
}
