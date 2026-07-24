package dio.budgeting.infrastructure.http;

import dio.budgeting.application.ai.BudgetChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/budget")
public class BudgetCommandController {

    private final BudgetChatService chatService;

    public BudgetCommandController(BudgetChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/text-command")
    public ResponseEntity<Map<String, String>> processTextCommand(@RequestBody Map<String, String> request) {
        String textCommand = request.getOrDefault("message", "");
        if (textCommand.isBlank()) {
            throw new IllegalArgumentException("Comando textual não pode ser vazio.");
        }

        String responseText = chatService.processNaturalLanguageCommand(textCommand);
        return ResponseEntity.ok(Map.of("response", responseText));
    }
}
