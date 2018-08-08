package pact.producer.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pact.producer.dto.ScoreUsernameTimestamp;
import pact.producer.exception.DuplicatedScoreException;
import pact.producer.exception.UserNotFoundException;
import pact.producer.service.TimeProvider;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ScoreHandlerTest {

  private static final String USER = "John";
  private static final int SCORE = 123;
  private static final Instant NOW = Instant.parse("2007-12-03T10:15:30.00Z");

  @Autowired
  private ScoreHandler scoreHandler;

  @MockBean
  private TimeProvider timeProvider;

  @BeforeEach
  void setUp() throws DuplicatedScoreException {
    when(timeProvider.now()).thenReturn(NOW);
    scoreHandler.createScore(USER, SCORE);
  }

  @Test
  @DisplayName("getAllScores should return all the scores")
  void getAllScores() {
    assertThat(scoreHandler.getAllScores(), containsInAnyOrder(new ScoreUsernameTimestamp(USER, SCORE, NOW)));
  }

  @Test
  @DisplayName("getScore should return the requested score")
  void getScore() throws UserNotFoundException {
    assertEquals(new ScoreUsernameTimestamp(USER, SCORE, NOW), scoreHandler.getScore(USER));
  }

  @Test
  @DisplayName("getScore should return an exception when the user is not found")
  void getScore_shouldReturnAnException_whenUserIsNotFound() throws UserNotFoundException {
    UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> scoreHandler.getScore("not-existent-user"));
    assertEquals("Could not find username: not-existent-user", ex.getMessage());
  }

  @Test
  @DisplayName("createScore should create a score")
  void createScore() throws DuplicatedScoreException, UserNotFoundException {
    scoreHandler.createScore("Pete", SCORE);
    assertEquals(new ScoreUsernameTimestamp("Pete", SCORE, NOW), scoreHandler.getScore("Pete"));
  }

  @Test
  @DisplayName("createScore should return an exceptinon when user is duplicated")
  void createScore_shouldReturnAnException_whenUserIsDuplicated() throws DuplicatedScoreException {
    DuplicatedScoreException ex = assertThrows(DuplicatedScoreException.class, () -> scoreHandler.createScore(USER, SCORE));
    assertEquals("Username John already exists", ex.getMessage());

  }

  @Test
  @DisplayName("updateScore should update a score")
  void updateScore() throws UserNotFoundException {
    scoreHandler.updateScore(USER, 124);
    assertEquals(new ScoreUsernameTimestamp(USER, 124, NOW), scoreHandler.getScore(USER));
  }

  @Test
  @DisplayName("updateScore should return an exception when the user is not found")
  void updateScore_shouldReturnAnException_whenUserIsNotFound() {
    UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> scoreHandler.updateScore("not-existent-user", 124));
    assertEquals("Could not find username: not-existent-user", ex.getMessage());
  }

  @Test
  @DisplayName("deleteScore should delete a score")
  void deleteScore() throws UserNotFoundException {
    scoreHandler.deleteScore(USER);
    assertThat(scoreHandler.getAllScores(), empty());
  }

  @Test
  @DisplayName("deleteScore should return an exception when the user is not found")
  void deleteScore_shouldReturnAnException_whenUserIsNotFound() throws UserNotFoundException {
    UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> scoreHandler.deleteScore("not-existent-user"));
    assertEquals("Could not find username: not-existent-user", ex.getMessage());
  }

}