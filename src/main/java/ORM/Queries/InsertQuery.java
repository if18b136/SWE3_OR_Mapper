package ORM.Queries;

import ORM.Annotations.Column;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;
import ORM.Parser;

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
            boolean first= true;
            insertQuery.append(operation).append(space);                    // "INSERT into" + " "

            insertQuery.append(entity.getTableName()).append(space);        // "Table Name" + " "
            StringBuilder columns = new StringBuilder();
            StringBuilder columnValues = new StringBuilder();
            columns.append(brOpen);                                         // "("
            columnValues.append(values).append(space).append(brOpen);       // "VALUES" + " " + "("

            Field[] fields = entity.getFields();
            for(int i = 0; i < fields.length; i++) {
                if((!fields[i].isAutoIncrement() || upsert) && fields[i].getValue(object) != null) { // with upsert check here every object that gets upserted  NEEDS a set PK!!!!
                    if(!first) {
                        columns.append(comma).append(space);
                        columnValues.append(comma).append(space);
                    } else {
                        first = false;
                    }

                    columns.append(fields[i].getColumnName());      // "--column Name--"
                    if (fields[i].isForeign()) {
                        String foreignColumnName = fields[i].getForeignColumn();
                        Entity foreignEntity = Manager.isCached(fields[i].getFieldType());
                        if (foreignEntity != null) {
                            // means we have a foreignKey custom class
                            Class<?> foreignClass = foreignEntity.getEntityClass();
                            try {
                                java.lang.reflect.Field foreignField = foreignClass.getDeclaredField(foreignColumnName);
                                foreignField.setAccessible(true);
                                columnValues.append(quotation).append(foreignField.get(fields[i].getValue(object))).append(quotation);      // "--column value--"
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else {
                            columnValues.append(quotation).append(fields[i].getValue(object)).append(quotation);      // "--column value--"
                        }
                    } else {
                        columnValues.append(quotation).append(fields[i].getValue(object)).append(quotation);      // "--column value--"
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
        } catch (InvalidParameterException ipe) {
            ipe.printStackTrace();
        }
        System.out.println(this.query);
        return query;
    }

}
