package use_case.search_player;

public interface SearchPlayerOutputBoundary {
    void present(SearchPlayerOutputData outputData);
    void presentPlayerNotFound(String message);
}

