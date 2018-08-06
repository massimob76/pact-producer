package pact.producer.controller;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyString;
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
import pact.producer.handler.ScoreHandler;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ScoreControllerTest {

    private static final String USER_NAME = "username";
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
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        when(scoreHandler.getAllScores()).thenReturn(singletonList(SCORE_USERNAME_TIMESTAMP));
        when(scoreHandler.getScore(anyString())).thenReturn(SCORE_USERNAME_TIMESTAMP);
    }

    @Test
    void getAllScores() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(singletonList(SCORE_USERNAME_TIMESTAMP))));
    }

    @Test
    void getScore() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + USER_NAME))
                .andExpect(status().isOk())
                .andExpect(content().json(convertToJson(SCORE_USERNAME_TIMESTAMP)));

        verify(scoreHandler).getScore(USER_NAME);
    }

    @Test
    void createScore() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .content(convertToJson(SCORE_USERNAME)))
                .andExpect(status().isAccepted());

        verify(scoreHandler).createScore(USER_NAME, SCORE);
    }

    @Test
    void updateScore() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + USER_NAME)
                .contentType(APPLICATION_JSON)
                .content(convertToJson(SCORE)))
                .andExpect(status().isAccepted());

        verify(scoreHandler).updateScore(USER_NAME, SCORE);
    }

    @Test
    void deleteScore() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + USER_NAME))
                .andExpect(status().isAccepted());

        verify(scoreHandler).deleteScore(USER_NAME);
    }

    private String convertToJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

}