package dio.budgeting.infrastructure.http;

import dio.budgeting.application.ai.BudgetChatService;
import dio.budgeting.application.ai.SpeechToTextService;
import dio.budgeting.application.ai.TextToSpeechService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-key"
})
class BudgetCommandControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    @MockBean
    private OpenAiAudioTranscriptionModel transcriptionModel;

    @MockBean
    private OpenAiAudioSpeechModel speechModel;

    @MockBean
    private SpeechToTextService speechToTextService;

    @MockBean
    private TextToSpeechService textToSpeechService;

    @MockBean
    private BudgetChatService budgetChatService;

    @Test
    void shouldProcessTextCommandSuccessfully() throws Exception {
        when(budgetChatService.processNaturalLanguageCommand(anyString()))
                .thenReturn("Receita registrada com sucesso.");

        Map<String, String> payload = Map.of("message", "Recebi meu salário de R$ 5000.");

        mockMvc.perform(post("/api/budget/text-command")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Receita registrada com sucesso."));
    }
}
