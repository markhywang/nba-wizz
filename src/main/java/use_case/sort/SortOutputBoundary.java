package use_case.sort;

public interface SortOutputBoundary {

    void present(SortOutputData outputData);

    void presentNoPlayers(String message);
}
