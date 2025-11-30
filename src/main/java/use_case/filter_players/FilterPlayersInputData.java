package use_case.filter_players;

import java.util.Set;
import java.util.Optional;

public record FilterPlayersInputData(Set<String> teams, Set<String> positions, Optional<Integer> seasonMin,
                                     Optional<Integer> seasonMax) {
}
