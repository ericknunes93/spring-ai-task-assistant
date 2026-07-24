package dio.budgeting.infrastructure.http;

import dio.budgeting.application.ai.BudgetChatService;
import dio.budgeting.application.ai.SpeechToTextService;
import dio.budgeting.application.ai.TextToSpeechService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/budget")
public class VoiceBudgetController {

    private final SpeechToTextService speechToTextService;
    private final BudgetChatService chatService;
    private final TextToSpeechService textToSpeechService;

    public VoiceBudgetController(SpeechToTextService speechToTextService,
                                 BudgetChatService chatService,
                                 TextToSpeechService textToSpeechService) {
        this.speechToTextService = speechToTextService;
        this.chatService = chatService;
        this.textToSpeechService = textToSpeechService;
    }

    /**
     * Endpoint de comando por voz.
     * Reutiliza integralmente o BudgetChatService (usado pelo text-command),
     * adicionando a etapa previa de Speech-To-Text (Whisper) e a sintese posterior de Text-To-Speech.
     */
    @PostMapping(value = "/voice-command", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processVoiceCommand(@RequestParam("file") MultipartFile audioFile) {
        if (audioFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Arquivo de áudio não pode estar vazio."));
        }

        try {
            // 1. Speech-To-Text (Whisper)
            String transcribedText = speechToTextService.transcribe(audioFile.getResource());

            // 2. ChatClient + Tool Calling (Reutilização do fluxo textual)
            String responseText = chatService.processNaturalLanguageCommand(transcribedText);

            // 3. Text-To-Speech (TTS)
            byte[] audioResponseBytes = textToSpeechService.synthesizeSpeech(responseText);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.setContentLength(audioResponseBytes.length);
            headers.add("X-Transcribed-Text", transcribedText);

            return new ResponseEntity<>(audioResponseBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro ao processar comando de voz: " + e.getMessage()));
        }
    }
}
