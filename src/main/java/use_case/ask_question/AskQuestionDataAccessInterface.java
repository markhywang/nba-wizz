package use_case.ask_question;

import java.io.IOException;

public interface AskQuestionDataAccessInterface {
    String getAnswer(String question, String context) throws IOException;
    String getDatasetContent() throws IOException;
}
