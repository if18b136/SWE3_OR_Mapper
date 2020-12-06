package ORM.Queries;

/**
 * generic interface for sql queries.
 */
public interface Query {
    /**
     * Targets that will be selected from database table.
     *
     * @param targets   targets as strings.
     */
    void addTargets(String... targets);
    /**
     * Tables that will be used as source for target selection.
     *
     * @param tables    tables as strings.
     */
    void addTables(String... tables);
    /**
     * Adding conditions for the targets.
     *
     * @param column    table column.
     * @param value     value that defines condition.
     * @param <T>       wildcard for generics.
     */
    <T> void addCondition(T column, T value);
    /**
     * Create string out of query.
     *
     * @return SQL query as string.
     */
    String buildQuery();
    /**
     * Update the query.
     */
    void updateQuery();
    /**
     * Retrieve the query as string.
     *
     * @return  Query as string.
     */
    String getQuery();
}
