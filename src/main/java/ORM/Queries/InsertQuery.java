package ORM.Queries;

import ORM.Base.Entity;
import ORM.Base.Field;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InsertQuery implements QueryLanguage {
    private final String operation = "INSERT into";
    String query;
    boolean upsert = false;

    //TODO needs to be documented as feature for user
    public void enableUpsert() {this.upsert = true;}
    public void disableUpsert() {this.upsert = false;}

    // Entity could be created to only need one argument,
    // but Manager handles entity creation so we would have to call Manager one extra time from here which is not resource friendly
    public String buildQuery(Object object, Entity entity) {
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

            Field[] fields = entity.getFields();
            for(int i = 0; i < (fields.length-1); i++) {
                if(!fields[i].isAutoIncrement() || upsert) { // with upsert check here every object that gets upserted  NEEDS a set PK!!!!
                    columns.append(fields[i].getColumnName()).append(comma).append(space);      // "column Name" + "," + " "
                    System.out.println(fields[i].getValue(object));
                    columnValues.append(quotation).append(fields[i].getValue(object)).append(quotation).append(comma).append(space);      // "column value" + "," + " "
                }
            }
            // last one needs extra append
            if(!fields[fields.length-1].isAutoIncrement()) {
                columns.append(fields[fields.length-1].getColumnName()).append(brClosed).append(space);
                System.out.println(fields[fields.length-1].getValue(object));
                columnValues.append(quotation).append(fields[fields.length-1].getValue(object)).append(quotation).append(brClosed);
            }

            // add on duplicate overwrite
            insertQuery.append(columns.toString()).append(columnValues.toString());

            if(upsert) {
                insertQuery.append(space).append(asNew).append(space).append(odku).append(space);     // "AS new" + " " + + "ON DUPLICATE KEY UPDATE" + " "
                StringBuilder upsertColumns = new StringBuilder();
                for(int i = 0; i < (fields.length-1); i++) {
                    if(!fields[i].isAutoIncrement() && !fields[i].isPrimary()) {
                        upsertColumns.append(fields[i].getColumnName()).append(eq).append("new").append(dot).append(fields[i].getColumnName()).append(comma).append(space);      // "colName" +"=" + "new" + "." + "colName" + "," + " "
                    }
                }
                if(!fields[fields.length-1].isAutoIncrement() && !fields[fields.length-1].isPrimary()) {
                    upsertColumns.append(fields[fields.length-1].getColumnName()).append(eq).append("new").append(dot).append(fields[fields.length-1].getColumnName());      // "colName" +"=" + "new" + "." + "colName"
                }
                insertQuery.append(upsertColumns.toString());
            }

            insertQuery.append(semicolon);
            this.query = insertQuery.toString();
        } catch (InvalidParameterException ipe) {
            ipe.printStackTrace();
        }
        return query;
    }

}
