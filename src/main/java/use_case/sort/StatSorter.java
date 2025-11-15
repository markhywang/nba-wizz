package use_case.sort;

import java.util.*;
import java.util.regex.Pattern;

public final class StatSorter {

    public enum Column {
        NAME, POS, AGE, TEAM, SEASON, G, MP,
        FG_PCT, P3_PCT, FT_PCT,
        TRB, AST, TOV, STL, BLK, PF, PTS
    }

    public enum Order { ASC, DESC }

    private Column lastColumn = null;
    private Order lastOrder = Order.DESC;

    public Column getLastColumn() { return lastColumn; }
    public Order getLastOrder() { return lastOrder; }

    /**
     * Sort in-place by the given column. If the same column is used consecutively,
     * the order toggles ASC<->DESC. For a new column, default is DESC.
     */
    public void sort(List<Map<String, Object>> rows, Column column) {
        Order next = (column == lastColumn)
                ? (lastOrder == Order.DESC ? Order.ASC : Order.DESC)
                : Order.DESC;
        sort(rows, column, next);
        lastColumn = column;
        lastOrder  = next;
    }

    /**
     * Sort in-place by the given column with an explicit order.
     */
    public static void sort(List<Map<String, Object>> rows, Column column, Order order) {
        if (rows == null || rows.isEmpty() || column == null) return;
        String key = headerOf(column);

        Comparator<Map<String, Object>> cmp = (a, b) -> {
            Object va = a.get(key);
            Object vb = b.get(key);

            // Missing values go to the end for both orders
            boolean ma = isMissing(va);
            boolean mb = isMissing(vb);
            if (ma || mb) {
                if (ma && mb) return 0;
                return 1 * (ma ? 1 : -1); // non-missing first
            }

            int base;
            if (isNumericLike(va) || isNumericLike(vb)) {
                double da = asNumber(va);
                double db = asNumber(vb);
                base = Double.compare(da, db);
            } else {
                base = toLower(va).compareTo(toLower(vb));
            }
            return order == Order.DESC ? -base : base;
        };

        rows.sort(cmp);
    }

    // ---- header mapping (must match CSV headers exactly) ----
    private static String headerOf(Column c) {
        return switch (c) {
            case NAME    -> "Name";
            case POS     -> "Pos";
            case AGE     -> "Age";
            case TEAM    -> "Team";
            case SEASON  -> "Season";
            case G       -> "G";
            case MP      -> "MP";
            case FG_PCT  -> "FG%";
            case P3_PCT  -> "3P%";
            case FT_PCT  -> "FT%";
            case TRB     -> "TRB";
            case AST     -> "AST";
            case TOV     -> "TOV";
            case STL     -> "STL";
            case BLK     -> "BLK";
            case PF      -> "PF";
            case PTS     -> "PTS";
        };
    }

    // ---- helpers ----
    private static final Pattern PCT = Pattern.compile("\\s*([-+]?\\d+(?:\\.\\d+)?)\\s*%\\s*");

    private static boolean isMissing(Object v) {
        if (v == null) return true;
        if (v instanceof String s) return s.trim().isEmpty();
        return false;
    }

    private static boolean isNumericLike(Object v) {
        if (v instanceof Number) return true;
        if (v instanceof String s) return looksLikeNumber(s) || PCT.matcher(s.trim()).matches();
        return false;
    }

    private static boolean looksLikeNumber(String s) {
        try { Double.parseDouble(s.trim()); return true; }
        catch (Exception e) { return false; }
    }

    /** Accepts Number, "45.6", "45.6%", "0.456". Returns 0.456 for 45.6%. */
    private static double asNumber(Object v) {
        if (v instanceof Number n) return n.doubleValue();
        String s = String.valueOf(v).trim();
        var m = PCT.matcher(s);
        if (m.matches()) return Double.parseDouble(m.group(1)) / 100.0;
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    private static String toLower(Object v) {
        return String.valueOf(v).toLowerCase(Locale.ROOT);
    }
}
