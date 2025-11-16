package use_case.compare;

import entity.Normalization;
import java.util.List;

public class CompareInputData {

    public enum EntityType { PLAYER, TEAM }

    private final EntityType entityType;
    private final List<String> entities;
    private final int SeasonStart;
    private final int SeasonEnd;
    private final String StatPreset;
    private final Normalization normalization;

    public CompareInputData(EntityType entityType, List<String> entities, int seasonStart, int seasonEnd,
                            String statPreset, Normalization normalization) {
        this.entityType = entityType;
        this.entities = entities;
        this.SeasonStart = seasonStart;
        this.SeasonEnd = seasonEnd;
        this.StatPreset = statPreset;
        this.normalization = normalization;
    }
    public EntityType getEntityType() { return entityType; }
    public List<String> getEntities() { return entities; }
    public int getSeasonStart() { return SeasonStart; }
    public int getSeasonEnd() { return SeasonEnd; }
    public String getStatPreset() { return StatPreset; }
    public Normalization getNormalization() { return normalization; }
}