package bootcamp.taskmanager.infrastructure.http;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class TaskAiController {

    private final ChatClient chatClient;

    public TaskAiController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chatWithAi(@RequestBody Map<String, String> request) {
        String userPrompt = request.getOrDefault("message", "");
        if (userPrompt.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mensagem não pode estar vazia"));
        }

        try {
            String aiResponse = chatClient.prompt()
                    .user(userPrompt)
                    .functions("criarTarefa", "listarTarefas", "atualizarStatusTarefa", "obterResumoTarefas")
                    .call()
                    .content();

            return ResponseEntity.ok(Map.of("response", aiResponse));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro ao processar IA: " + e.getMessage()));
        }
    }
}
