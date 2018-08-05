package pact.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import pact.producer.handler.ScoreHandler;
import pact.producer.model.Score;

import java.time.Instant;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ScoreControllerTest {

    private static final String USER_NAME = "username";
    private static final Score SCORE = new Score(USER_NAME, 1, Instant.parse("2018-08-05T19:56:16.685Z"));
    private static final String BASE_PATH = "/api/v1/scores";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScoreHandler scoreHandler;

    private MockMvc mockMvc;

    private String scoreAsJson;

    @BeforeEach
    void setup() throws JsonProcessingException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        when(scoreHandler.getAllScores()).thenReturn(singletonList(SCORE));
        when(scoreHandler.getScore(anyString())).thenReturn(SCORE);
        scoreAsJson = objectMapper.writeValueAsString(SCORE);
    }

    @Test
    void getAllScores() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json("[" + scoreAsJson + "]"));
    }

    @Test
    void getScore() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + USER_NAME))
                .andExpect(status().isOk())
                .andExpect(content().json(scoreAsJson));

        verify(scoreHandler).getScore(USER_NAME);
    }

    @Test
    void createScore() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .content(scoreAsJson))
                .andExpect(status().isAccepted());

        verify(scoreHandler).createScore(SCORE);
    }

    @Test
    void updateScore() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/" + USER_NAME)
                .contentType(APPLICATION_JSON)
                .content(scoreAsJson))
                .andExpect(status().isAccepted());

        verify(scoreHandler).updateScore(USER_NAME, SCORE);
    }

    @Test
    void deleteScore() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + USER_NAME))
                .andExpect(status().isAccepted());

        verify(scoreHandler).deleteScore(USER_NAME);
    }

}