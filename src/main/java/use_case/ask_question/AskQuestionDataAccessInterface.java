package use_case.ask_question;

import java.io.IOException;
import java.util.function.Consumer;

public interface AskQuestionDataAccessInterface {
    void getAnswer(String question, String context, Consumer<String> onData, Runnable onComplete, Consumer<Exception> onError) throws IOException;
    String getDatasetContent() throws IOException;
}
