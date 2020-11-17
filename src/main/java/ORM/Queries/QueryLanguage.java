package ORM.Queries;

public interface QueryLanguage {
    char space = ' ';
    char all = '*';
    char eq = '=';
    char comma = ',';
    String from = "FROM";
    String and = "AND";
    String where = "WHERE";
    char semicolon = ';';
    char brOpen = '(';
    char brClosed = ')';
    String values = "VALUES";
    char quotation = '"';
    String asNew = "AS new";
    String odku = "ON DUPLICATE KEY UPDATE";
    char dot = '.';
}
