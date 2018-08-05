package pact.producer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.Instant;

public class Score {

    private final String name;
    private final int value;
    private final Instant timestamp;

    @JsonCreator
    public Score(@JsonProperty("name") String name, @JsonProperty("value") int value, @JsonProperty("timestamp") Instant timestamp) {
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Score score = (Score) o;

        if (value != score.value) return false;
        if (name != null ? !name.equals(score.name) : score.name != null) return false;
        return timestamp != null ? timestamp.equals(score.timestamp) : score.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + value;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
