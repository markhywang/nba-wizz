package entity;

/**
 * How stats should be scaled for comparison.
 * PER_GAME: use the pre-game averages from the CSV.
 * PER_36: scale counting stats to per-36-minutes using MP.
 */
public enum Normalization {
    PER_GAME,
    PER_36
}