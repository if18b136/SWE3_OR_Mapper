package ORM.Queries;

import Database.DatabaseConnection;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.MetaData;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sql query class for insert queries.
 */
public class InsertQuery implements QueryLanguage {
    /**
     * INSERT into sql string.
     */
    protected final String operation = "INSERT into";
    /**
     * Full insert sql query
     */
    String query;
    /**
     * Boolean var for upsert.
     */
    boolean upsert = false;
    /**
     * Prepared statement to prevent sql injection.
     */
    private PreparedStatement stmt;

    //TODO needs to be documented as feature for user

    /**
     * Enables upsert part of buildQuery() function.
     */
    public void enableUpsert() {this.upsert = true;}

    /**
     * Disables upsert part of buildQuery() function.
     */
    public void disableUpsert() {this.upsert = false;}



    /**
     * Constructs a full insert or upsert query for a custom entity.
     * Entity could be created to only need one argument,
     * but Manager handles entity creation so we would have to call Manager one extra time from here which is not resource friendly
     *
     * @param object    Object containing variables for sql query.
     * @param entity    Custom class entity.
     */
    public void buildQuery(Object object, Entity entity) {
        try{
            if (entity.getTableName().isEmpty()) {
                throw new InvalidParameterException("Entity :" + entity.toString() + " has no table name");
            }
            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append(operation).append(space);                    // "INSERT into" + " "
            insertQuery.append(entity.getTableName()).append(space);        // "Table Name" + " "

            StringBuilder columns = new StringBuilder();
            StringBuilder columnValues = new StringBuilder();
            columns.append(brOpen);                                         // "("
            columnValues.append(values).append(space).append(brOpen);       // "VALUES" + " " + "("

            List<Object> values = new ArrayList<>();
            boolean first = true;
            Field[] fields = entity.getFields();

            for ( Field field : fields) {
                if(!field.isAutoIncrement()) { //AI does not need manual insertion - could be expanded with set value check
                    if(first) {
                        first = false;
                    } else {
                        if(field.getValue(object) != null) {
                            columns.append(comma).append(space);
                            columnValues.append(comma).append(space);
                        }
                    }

                    if (field.isForeign()) {                    // get the type and value of the foreign object
                        if (field.getValue(object) != null) {
                            columns.append(field.getColumnName());      // append col Name
                            columnValues.append(question);              // add anti-SQL-Injection question mark
                            values.add(MetaData.toColumnType(field,field.getValue(object)));
                        } else if (!field.isNullable()) {
                            throw new NullPointerException(object + ": non-nullable Field " + field.getColumnName() + " is not set.");
                        } // else { do nothing because then not inserting a value in the column is valid }
                    } else {    // we can be sure that all internal fields will be non-custom types so the extra transform is not needed here (even for localDate to SQL Date)
                        columns.append(field.getColumnName());      // append col Name
                        columnValues.append(question);              // add anti-SQL-Injection question mark
                        values.add(field.getValue(object));         // add value to object array from which it can be inserted into the prepared statement later
                    }
                }
            }

            columns.append(brClosed).append(space);
            columnValues.append(brClosed);

            // add on duplicate overwrite
            insertQuery.append(columns.toString()).append(columnValues.toString());
            first = true;

            if(upsert) {
                insertQuery.append(space).append(asNew).append(space).append(odku).append(space);     // "AS new" + " " + + "ON DUPLICATE KEY UPDATE" + " "
                StringBuilder upsertColumns = new StringBuilder();
                for(int i = 0; i < fields.length; i++) {
                    if(!fields[i].isAutoIncrement() && !fields[i].isPrimary() && fields[i].getValue(object) != null) {
                        if(!first) {
                            upsertColumns.append(comma).append(space);
                        } else {
                            first = false;
                        }
                        upsertColumns.append(fields[i].getColumnName()).append(eq).append("new").append(dot).append(fields[i].getColumnName());      // "colName" +"=" + "new" + "." + "colName" + "," + " "
                    }
                }
                insertQuery.append(upsertColumns.toString());
            }

            insertQuery.append(semicolon);
            this.query = insertQuery.toString();

            Connection db = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = db.prepareStatement(insertQuery.toString());
            int i = 1;
            for (Object obj : values) {
                stmt.setObject(i++,obj);
            }
            this.stmt = stmt;

        } catch (InvalidParameterException | SQLException ipe) {
            ipe.printStackTrace();
        }
    }

    /**
     * Returns the sql query as prepared statement.
     *
     * @return  the sql query as prepared statement.
     */
    public PreparedStatement getStmt() { return this.stmt; }

    /**
     * Returns the sql query as string.
     *
     * @return the sql query as string.
     */
    public String getQuery() { return this.query; }

}
