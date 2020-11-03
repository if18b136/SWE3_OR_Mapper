package ORM;

import Database.DatabaseConnection;
import ORM.Annotations.Table;
import ORM.Base.Entity;
import ORM.Base.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public final class Statement {
    static final Logger statementLogger = LogManager.getLogger("Statement Logger");
    private Statement() {}

    public static String initTable(Class<?> tableClass) {
        List<MetaData.fieldData> fields = MetaData.classMetaData(tableClass);
        StringBuilder initTable = new StringBuilder();
        initTable.append("CREATE TABLE ").append(MetaData.getAnnotationTableName(tableClass)).append(" ( ");   // TODO clean up class name extraction
        for(MetaData.fieldData field : fields) {
            initTable.append(field.name).append(" ").append(Parser.parseType(field.type)).append(", ");
        }
        initTable.append("PRIMARY KEY (").append(fields.get(0).name).append(") );");    //set the first value to primary key - temp solve for basic tables
        return initTable.toString();
    }

    public static String initFromClass(Class<?> tableClass) {
        List<String> data = MetaData.getAnnotationColumnData(tableClass);
        StringBuilder initTable = new StringBuilder();
        initTable.append("CREATE TABLE ").append(MetaData.getAnnotationTableName(tableClass)).append(" (");
        for ( String sql : data) {
            initTable.append(sql).append(" ");
        }
        initTable.append(");");
        return initTable.toString();
    }

    public static String insert(Entity entity) {
        Field[] fields = entity.getFields();
        // if fields has primary field:
        int i = Utility.hasPK(fields).size() > 0 ? 1 : 0;

        StringBuilder insertStatement = new StringBuilder();
        insertStatement.append("INSERT into ").append(entity.getTableName()).append(" ");
        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        names.append("(");
        values.append(" VALUES (");
        if(i == 0) {
            for( ; i < (fields.length-1) ; i++) {
                names.append(fields[i].getColumnName()).append(", ");
                values.append("\"").append(fields[i].getValue()).append("\"").append(", ");
            }
            // last one needs extra append
            names.append(fields[fields.length-1].getColumnName()).append(") ");
            values.append("\"").append(fields[fields.length-1].getValue()).append("\"").append(")");
        } else {
            for( int j : Utility.noPKFields(fields)) {
                names.append(fields[j].getColumnName());
                values.append("\"").append(fields[j].getValue()).append("\"");
                if(fields[j].equals(fields[fields.length-1])) { // if the last field is an AI field, we need to append different
                    names.append(") ");
                    values.append(")");
                } else {
                    names.append(", ");
                    values.append(", ");
                }
            }
        }

        insertStatement.append(names.toString()).append(values.toString()).append(";");
        return insertStatement.toString();
    }


}
