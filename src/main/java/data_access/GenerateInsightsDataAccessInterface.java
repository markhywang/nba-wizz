package data_access;

/**
 * Backwards-compatible adapter interface that delegates to the
 * use_case-level `GenerateInsightsDataAccessInterface`.
 * Keeping this adapter avoids having to change all existing imports
 * at once while the codebase is transitioned to the Clean Architecture
 * placement of the interface in `use_case.generate_insights`.
 */
public interface GenerateInsightsDataAccessInterface extends use_case.generate_insights.GenerateInsightsDataAccessInterface {

}
