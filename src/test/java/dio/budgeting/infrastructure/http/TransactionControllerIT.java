package dio.budgeting.infrastructure.http;

import dio.budgeting.application.ai.SpeechToTextService;
import dio.budgeting.application.ai.TextToSpeechService;
import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionType;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-key"
})
class TransactionControllerIT {

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

    @Test
    void shouldCreateAndRetrieveTransactionsViaREST() throws Exception {
        CreateTransactionCommand command = new CreateTransactionCommand(
                new BigDecimal("250.00"),
                TransactionType.RECEITA,
                TransactionCategory.SALARIO,
                "Bônus de projeto"
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.type").value("RECEITA"));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/transactions/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceitas").exists())
                .andExpect(jsonPath("$.saldoAtual").exists());
    }
}
