package pact.producer.controller;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pact.producer.dto.ScoreUsername;
import pact.producer.dto.ScoreUsernameTimestamp;
import pact.producer.exception.DuplicatedScoreException;
import pact.producer.exception.UserNotFoundException;
import pact.producer.handler.ScoreHandler;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ScoreControllerTest {

    private static final String USER_NAME = "john";
    private static final int SCORE = 123;
    private static final Instant TIMESTAMP = Instant.parse("2018-08-05T19:56:16.685Z");
    private static final ScoreUsername SCORE_USERNAME = new ScoreUsername(USER_NAME, SCORE);
    private static final ScoreUsernameTimestamp SCORE_USERNAME_TIMESTAMP = new ScoreUsernameTimestamp(USER_NAME, SCORE, TIMESTAMP);
    private static final String BASE_PATH = "/api/v1/scores";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScoreHandler scoreHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() throws UserNotFoundException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        when(scoreHandler.getAllScores()).thenReturn(singletonList(SCORE_USERNAME_TIMESTAMP));
        when(scoreHandler.getScore(anyString())).thenReturn(SCORE_USERNAME_TIMESTAMP);
    }

    @Test
    @DisplayName("GET on " + BASE_PATH + " should return all the scores")
    void getAllScores() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(singletonList(SCORE_USERNAME_TIMESTAMP))));
    }

    @Test
    @DisplayName("GET on " + BASE_PATH + " should return a 500 when internal exception")
    void getAllScores_shouldReturnA500_whenInternalException() throws Exception {
        when(scoreHandler.getAllScores()).thenThrow(new RuntimeException("Internal Server Exception"));

        mockMvc.perform(get(BASE_PATH))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json("{\"error\":\"Internal Server Exception\"}"));
    }

    @Test
    @DisplayName("GET on " + BASE_PATH + "/{username} should return the username score")
    void getScore_shouldReturnScoreForUser() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + USER_NAME))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(SCORE_USERNAME_TIMESTAMP)));

        verify(scoreHandler).getScore(USER_NAME);
    }

    @Test
    @DisplayName("GET on " + BASE_PATH + "/{username} should return a 404 when user not found")
    void getScore_shouldReturnA404_whenUserNotFound() throws Exception {
        when(scoreHandler.getScore(anyString())).thenThrow(new UserNotFoundException("Could not find username: " + USER_NAME));

        mockMvc.perform(get(BASE_PATH + "/" + USER_NAME))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"error\":\"Could not find username: " + USER_NAME + "\"}"));
    }

    @Test
    @DisplayName("POST on " + BASE_PATH + "should create a new score")
    void createScore() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .content(convertToJson(SCORE_USERNAME)))
                .andExpect(status().isAccepted());

        verify(scoreHandler).createScore(USER_NAME, SCORE);
    }

    @Test
    @DisplayName("POST on " + BASE_PATH + "should return a 400 when username is null")
    void createScore_shouldReturnA400_whenUsernameIsNull() throws Exception {
        mockMvc.perform(post(BASE_PATH)
            .contentType(APPLICATION_JSON)
            .content("{\"score\":\"124\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().json("{\"error\":\"Validation failed for argument at index 0 in method: void pact.producer.controller.ScoreController.createScore(pact.producer.dto.ScoreUsername) throws pact.producer.exception.DuplicatedScoreException, with 1 error(s): [Field error in object 'scoreUsername' on field 'name': rejected value [null]; codes [NotNull.scoreUsername.name,NotNull.name,NotNull.java.lang.String,NotNull]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [scoreUsername.name,name]; arguments []; default message [name]]; default message [must not be null]] \"}"));
    }

    @Test
    @DisplayName("POST on " + BASE_PATH + "should return a 400 when duplicated score")
    void createScore_shouldReturnA400_whenDuplicatedScore() throws Exception {
        doThrow(new DuplicatedScoreException("Username " + USER_NAME + " already exists")).when(scoreHandler).createScore(anyString(), anyInt());

        mockMvc.perform(post(BASE_PATH)
            .contentType(APPLICATION_JSON)
            .content(convertToJson(SCORE_USERNAME)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json("{\"error\":\"Username " + USER_NAME + " already exists\"}"));
    }

    @Test
    @DisplayName("PUT on " + BASE_PATH + "/{username} should update the score")
    void updateScore() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + USER_NAME)
                .contentType(APPLICATION_JSON)
                .content(convertToJson(SCORE)))
                .andExpect(status().isAccepted());

        verify(scoreHandler).updateScore(USER_NAME, SCORE);
    }

    @Test
    @DisplayName("PUT on " + BASE_PATH + "/{username} should return a 400 when score is missing")
    void updateScore_shouldReturnA400_whenScoreIsMissing() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + USER_NAME)
            .contentType(APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().json("{\"error\":\"JSON parse error: Cannot deserialize instance of `int` out of START_OBJECT token; nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize instance of `int` out of START_OBJECT token\\n at [Source: (PushbackInputStream); line: 1, column: 1]\"}"));
    }

    @Test
    @DisplayName("PUT on " + BASE_PATH + "/{username} should return a 404 when user not found")
    void updateScore_shouldReturnA404_whenUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("Could not find username: " + USER_NAME)).when(scoreHandler).updateScore(anyString(), anyInt());

        mockMvc.perform(put(BASE_PATH + "/" + USER_NAME)
            .contentType(APPLICATION_JSON)
            .content(convertToJson(SCORE)))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"error\":\"Could not find username: " + USER_NAME + "\"}"));
    }

    @Test
    @DisplayName("DELETE on " + BASE_PATH + "/{username} should delete the score")
    void deleteScore() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + USER_NAME))
                .andExpect(status().isAccepted());

        verify(scoreHandler).deleteScore(USER_NAME);
    }

    @Test
    @DisplayName("DELETE on " + BASE_PATH + "/{username} should return a 4040 when user not found")
    void deleteScore_shouldReturnA404_whenUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("Could not find username: " + USER_NAME)).when(scoreHandler).deleteScore(anyString());

        mockMvc.perform(delete(BASE_PATH + "/" + USER_NAME))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"error\":\"Could not find username: " + USER_NAME + "\"}"));
    }

    private String convertToJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

}