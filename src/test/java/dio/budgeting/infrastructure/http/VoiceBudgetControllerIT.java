package dio.budgeting.infrastructure.http;

import dio.budgeting.application.ai.BudgetChatService;
import dio.budgeting.application.ai.SpeechToTextService;
import dio.budgeting.application.ai.TextToSpeechService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-key"
})
class VoiceBudgetControllerIT {

    @Autowired
    private MockMvc mockMvc;

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
    void shouldProcessVoiceCommandSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "audio.mp3",
                "audio/mpeg",
                "fake-audio-bytes".getBytes()
        );

        when(speechToTextService.transcribe(any())).thenReturn("Gastei 50 reais");
        when(budgetChatService.processNaturalLanguageCommand("Gastei 50 reais")).thenReturn("Despesa salva");
        when(textToSpeechService.synthesizeSpeech("Despesa salva")).thenReturn("fake-mp3-bytes".getBytes());

        mockMvc.perform(multipart("/api/budget/voice-command").file(file))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Transcribed-Text", "Gastei 50 reais"))
                .andExpect(content().contentType("audio/mpeg"));
    }
}
