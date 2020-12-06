package ORM.Queries;

/**
 * Helper interface for various sql query language.
 * Implemented in all query classes to circumvent the need to append strings
 */
public interface QueryLanguage {
    /**
     * space character
     */
    char space = ' ';
    /**
     * asterisk character
     */
    char all = '*';
    /**
     * equals character
     */
    char eq = '=';
    /**
     * comma character
     */
    char comma = ',';
    /**
     * FROM sql string
     */
    String from = "FROM";
    /**
     * AND sql string
     */
    String and = "AND";
    /**
     * WHERE sql string
     */
    String where = "WHERE";
    /**
     * semicolon character
     */
    char semicolon = ';';
    /**
     * opening bracket character
     */
    char brOpen = '(';
    /**
     * closing bracket character
     */
    char brClosed = ')';
    /**
     * VALUES sql string
     */
    String values = "VALUES";
    /**
     * quotation mark character
     */
    char quotation = '"';
    /**
     * AS new sql string
     */
    String asNew = "AS new";
    /**
     * ON DUPLICATE KEY UPDATE sql string
     */
    String odku = "ON DUPLICATE KEY UPDATE";
    /**
     * dot character
     */
    char dot = '.';
    /**
     * PRIMARY KEY sql string
     */
    String primary = "PRIMARY KEY";
    /**
     * FOREIGN KEY sql string
     */
    String foreign = "FOREIGN KEY";
    /**
     * AUTO_INCREMENT sql string
     */
    String autoInc = "AUTO_INCREMENT";
    /**
     * UNIQUE sql string
     */
    String unique = "UNIQUE";
    /**
     * NOT NULL sql string
     */
    String notNull = "NOT NULL";
    /**
     * REFERENCES sql string
     */
    String reference = "REFERENCES";
    /**
     * question mark character
     */
    char question = '?';
    /**
     * JOIN sql string
     */
    String join = "JOIN";
    /**
     * ON sql string
     */
    String on = "ON";
}
