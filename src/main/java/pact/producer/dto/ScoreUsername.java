package pact.producer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class ScoreUsername {

    @NotNull
    private final String name;

    @NotNull
    private final int score;

    @JsonCreator
    public ScoreUsername(@JsonProperty("name") String name, @JsonProperty("score") int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScoreUsername that = (ScoreUsername) o;
        return score == that.score &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }

    @Override
    public String toString() {
        return "ScoreUsername{" +
            "name='" + name + '\'' +
            ", score=" + score +
            '}';
    }
}
