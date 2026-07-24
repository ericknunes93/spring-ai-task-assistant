package bootcamp.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-key"
})
class TaskmanagerApplicationTest {

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    @Test
    void contextLoads() {
    }
}
