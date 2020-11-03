package ORM.Queries;

public interface Query {
    //TODO add table joins

    char space = ' ';
    char all = '*';
    char eq = '=';
    char comma = ',';
    String from = "FROM";
    String and = "AND";
    String where = "WHERE";
    char semicolon = ';';

    void addTargets(String... targets);
    void addTables(String... tables);
    <T> void addCondition(T column, T value);
    String buildQuery();
    void updateQuery();
    String getQuery();
}
