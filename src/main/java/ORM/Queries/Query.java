package ORM.Queries;

/**
 * generic interface for sql queries.
 */
public interface Query {
    //TODO add table joins

    void addTargets(String... targets);
    void addTables(String... tables);
    <T> void addCondition(T column, T value);
    String buildQuery();
    void updateQuery();
    String getQuery();
}
