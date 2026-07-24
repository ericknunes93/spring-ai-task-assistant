package dio.budgeting.application.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * SRP: Centraliza a comunicação com o ChatClient, gerencia o Tool Calling
 * e orquestra o raciocínio em linguagem natural sobre as ferramentas financeiras.
 */
@Service
public class BudgetChatService {

    private final ChatClient chatClient;

    public BudgetChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String processNaturalLanguageCommand(String textCommand) {
        return chatClient.prompt()
                .user(textCommand)
                .functions("criarTransacao", "listarTransacoes", "obterResumoFinanceiro")
                .call()
                .content();
    }
}
