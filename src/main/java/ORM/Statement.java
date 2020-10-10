package ORM;

import java.util.List;

/**
 *
 */
public class Statement {

    // TODO make a check for the table name before operation
    public String insert(Object obj, String table) throws IllegalAccessException {  // TODO - exception is there because of MetaData.fieldData method - refactor if more exceptions could occur
        // extract all attributes from obj - reflection
        //INSERT into $table ($att1,$att2,...) VALUES ($val1,$val2,...);
        StringBuilder insertStatement = new StringBuilder();
        insertStatement.append("INSERT into ").append(table).append(" ");
        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        names.append("(");
        values.append(" VALUES (");

        MetaData metaData = new MetaData();
        List<MetaData.fieldData> objectFields = metaData.fields(obj);
        for( MetaData.fieldData field : objectFields) {     // TODO refactor so that no if needed? last value after loop
            names.append(field.name);
            values.append(field.value);
            if (field.equals(objectFields.get(objectFields.size() - 1))) {  // bad solution code-wise
                names.append(") ");
                values.append(")");
            } else {
                names.append(", ");
                values.append(", ");
            }
        }
        insertStatement.append(names.toString()).append(values.toString()).append(";");
        return insertStatement.toString();
    }
}
