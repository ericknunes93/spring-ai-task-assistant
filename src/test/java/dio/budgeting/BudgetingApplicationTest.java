package dio.budgeting;

import dio.budgeting.application.ai.SpeechToTextService;
import dio.budgeting.application.ai.TextToSpeechService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-key"
})
class BudgetingApplicationTest {

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
    void contextLoads() {
    }
}
