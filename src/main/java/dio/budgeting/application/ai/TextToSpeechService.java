package dio.budgeting.application.ai;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.stereotype.Service;

/**
 * SRP: Responsável exclusivamente pela síntese de texto em áudio (Text-To-Speech)
 * utilizando o modelo OpenAiAudioSpeechModel.
 */
@Service
public class TextToSpeechService {

    private final OpenAiAudioSpeechModel speechModel;

    public TextToSpeechService(OpenAiAudioSpeechModel speechModel) {
        this.speechModel = speechModel;
    }

    public byte[] synthesizeSpeech(String text) {
        SpeechPrompt prompt = new SpeechPrompt(text);
        return speechModel.call(prompt).getResult().getOutput();
    }
}
