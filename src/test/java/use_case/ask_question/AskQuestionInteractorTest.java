package use_case.ask_question;

import entity.Answer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.function.Consumer;

class AskQuestionInteractorTest {

    private AskQuestionDataAccessInterface dataAccess;
    private AskQuestionOutputBoundary presenter;
    private AskQuestionInteractor interactor;

    @BeforeEach
    void setUp() {
        // Default behavior for successful cases
        dataAccess = new MockAskQuestionDataAccess();
        presenter = new MockAskQuestionPresenter();
        interactor = new AskQuestionInteractor(dataAccess, presenter);
    }

    @Test
    void execute_withValidQuestion_shouldPrepareSuccessView() {
        AskQuestionInputData inputData = new AskQuestionInputData("Who is the GOAT?");
        interactor.execute(inputData);

        MockAskQuestionPresenter mockPresenter = (MockAskQuestionPresenter) presenter;
        assertTrue(mockPresenter.isSuccess());
        assertNotNull(mockPresenter.getOutputData());
        assertEquals("Michael Jordan is the GOAT.", mockPresenter.getOutputData().getAnswer().getResponse());
    }

    @Test
    void execute_withEmptyQuestion_shouldPrepareFailView() {
        AskQuestionInputData inputData = new AskQuestionInputData(" ");
        interactor.execute(inputData);

        MockAskQuestionPresenter mockPresenter = (MockAskQuestionPresenter) presenter;
        assertFalse(mockPresenter.isSuccess());
        assertEquals("Question cannot be empty.", mockPresenter.getError());
    }

    @Test
    void execute_withDataAccessIOException_shouldPrepareFailView() {
        dataAccess = new MockAskQuestionDataAccess() {
            @Override
            public String getDatasetContent() throws IOException {
                throw new IOException("Database error");
            }
        };
        interactor = new AskQuestionInteractor(dataAccess, presenter);
        AskQuestionInputData inputData = new AskQuestionInputData("Test question");
        interactor.execute(inputData);

        MockAskQuestionPresenter mockPresenter = (MockAskQuestionPresenter) presenter;
        assertFalse(mockPresenter.isSuccess());
        assertTrue(mockPresenter.getError().contains("Error reading dataset"));
    }

    @Test
    void execute_withInappropriateQuestion_shouldPrepareFailView() {
        dataAccess = new MockAskQuestionDataAccess() {
            @Override
            public void getAnswer(String question, String context, Consumer<String> onData, Runnable onComplete, Consumer<Exception> onError) {
                onData.accept("I cannot answer this question.");
                onComplete.run();
            }
        };
        interactor = new AskQuestionInteractor(dataAccess, presenter);
        AskQuestionInputData inputData = new AskQuestionInputData("Inappropriate question");
        interactor.execute(inputData);

        MockAskQuestionPresenter mockPresenter = (MockAskQuestionPresenter) presenter;
        assertTrue(mockPresenter.isSuccess());
        assertEquals("I cannot answer this question.", mockPresenter.getOutputData().getAnswer().getResponse());
    }

    // Mock implementations for testing
    private static class MockAskQuestionDataAccess implements AskQuestionDataAccessInterface {
        @Override
        public void getAnswer(String question, String context, Consumer<String> onData, Runnable onComplete, Consumer<Exception> onError) {
            onData.accept("Michael Jordan is the GOAT.");
            onComplete.run();
        }

        @Override
        public String getDatasetContent() throws IOException {
            return "CSV,Data,Here";
        }
    }

    private static class MockAskQuestionPresenter implements AskQuestionOutputBoundary {
        private boolean success = false;
        private String error = null;
        private AskQuestionOutputData outputData;
        private boolean loading = false;
        private StringBuilder partialResponse = new StringBuilder();

        @Override
        public void prepareSuccessView(AskQuestionOutputData outputData) {
            this.success = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.success = false;
            this.error = error;
        }

        @Override
        public void presentLoading() {
            this.loading = true;
        }

        @Override
        public void presentPartialResponse(AskQuestionOutputData outputData) {
            this.success = true;
            this.outputData = outputData;
            this.partialResponse.append(outputData.getAnswer().getResponse());
        }

        @Override
        public void presentStreamingComplete() {
            this.loading = false;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }

        public AskQuestionOutputData getOutputData() {
            return outputData;
        }
    }
}
