package ORM.Queries;

import Database.DatabaseConnection;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteQuery implements QueryLanguage{
    private final String operation = "DELETE from";
    private String queryString;
    private PreparedStatement stmt;

    public void buildQuery(Object object, Entity entity) {
        StringBuilder query = new StringBuilder();
        query.append(operation);
        query.append(space).append(entity.getTableName()).append(space).append(where);
        boolean first = true;
        List<Object> values = new ArrayList<>();

        for (Field field : entity.getPrimaryFields()) {
            if (!first) {
                query.append(space).append(and);
            } else {
                first = false;
            }
            query.append(space).append(field.getColumnName()).append(space).append(eq).append(space).append(question);
            // Needs to get the foreign key database value instead of the object it is representing.
            if ( field.isForeign() && field.getValue(object) != null) {  // the null comparison omits empty values, on delete cascade, on update set null, etc.
                //this only works once - if the fk points to a object which first pk is another fk object this won't work - would need a recursive call until a non-custom object type is retrieved.
                Object obj = field.getValue(object);
                Entity ent = Manager.getEntity(obj);
                values.add(ent.getPrimaryFields()[0].getValue(obj));
            } else {
                values.add(field.getValue(object));
            }
        }
        query.append(semicolon);
        this.queryString = query.toString();

        try {
            Connection db = DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement = db.prepareStatement(query.toString());
            int i = 1;
            for (Object obj : values) {
                statement.setObject(i++, obj);
            }
            this.stmt = statement;
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    public String getQuery() { return this.queryString; }
    public PreparedStatement getStmt() { return this.stmt; }
}
