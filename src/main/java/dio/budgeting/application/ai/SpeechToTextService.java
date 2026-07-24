package dio.budgeting.application.ai;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * SRP: Responsável exclusivamente pela transcrição de áudio em texto (Speech-To-Text)
 * utilizando o modelo Whisper (OpenAiAudioTranscriptionModel).
 */
@Service
public class SpeechToTextService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public SpeechToTextService(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    public String transcribe(Resource audioResource) {
        var response = transcriptionModel.call(new AudioTranscriptionPrompt(audioResource));
        return response.getResult().getOutput();
    }
}
