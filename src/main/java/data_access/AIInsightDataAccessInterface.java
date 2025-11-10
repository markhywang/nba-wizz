package data_access;

import entity.AIInsight;
import java.util.List;

public interface AIInsightDataAccessInterface extends DataAccessInterface<AIInsight> {
    List<AIInsight> findByEntity(String entityName);
}
