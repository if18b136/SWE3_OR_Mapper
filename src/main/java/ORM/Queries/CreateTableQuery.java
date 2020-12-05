package ORM.Queries;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;
import ORM.MetaData;
import ORM.Parser;

import java.lang.annotation.Annotation;
import java.util.List;

public class CreateTableQuery implements QueryLanguage {
    private final String operation = "CREATE TABLE";
    String query;

    public String buildQuery(Entity entity) {
        StringBuilder createTable = new StringBuilder();
        createTable.append(operation).append(space).append(entity.getTableName()).append(space).append(brOpen);    // "CREATE TABLE" + " " + "--table Name--" + " " + "("

        boolean first = true;

        //fk and m:n need to be recognised - MetaData doesn't do that currently
        //TODO update/delete handling in table creation
        for(Field field : entity.getFields()) {
            Annotation[] annotations = field.getField().getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Column) {
                    if(field.getFieldType().equals(List.class)) {   // means that this will be a foreign key to another table
                        // basically do nothing here - the other table will reference on this table with a fk.
                        break;
                    } else if (field.getFieldType().getComponentType() != null) {
                        // same here, it's just an check if the type is an Array
                        break;
                    } else {
                        if(!first) { createTable.append(comma).append(space); } // every additional entry needs a ", " at the beginning
                        createTable.append(field.getColumnName()).append(space);        // "--column Name--" + " "
                        if(field.isForeign()) { // TODO cleanup
                            String foreignColumnName = field.getForeignColumn();
                            Entity foreignEntity = Manager.getEntityIfExists(field.getFieldType());
                            if(foreignEntity != null) {
                                Class<?> foreignClass = foreignEntity.getEntityClass();
                                try {
                                    java.lang.reflect.Field foreignField = foreignClass.getDeclaredField(foreignColumnName);
                                    createTable.append(Parser.parseType(foreignField.getType().getName(),((Column) annotation).length()));
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                }
                            } else {    // Set to foreign key but is not a custom class - means user better made sure to assign the same type to both fields
                                createTable.append(Parser.parseType(field.getField().getType().getName(),((Column) annotation).length()));
                            }
                        } else {
                            createTable.append(Parser.parseType(field.getField().getType().getName(),((Column) annotation).length()));
                        }

                        if(((Column) annotation).primary()) { createTable.append(space).append(primary); }
                        if (((Column) annotation).autoIncrement()) { createTable.append(space).append(autoInc); }
                        if (((Column) annotation).unique()) { createTable.append(space).append(unique); }
                        if (!((Column) annotation).nullable() || !((Column) annotation).primary()) { createTable.append(space).append(notNull); }

                        first = false;
                    }
                } else if (annotation instanceof ForeignKey) {
                    createTable.append(comma).append(space).append(foreign).append(space).append(brOpen);
                    createTable.append(field.getColumnName()).append(brClosed);
                    createTable.append(space).append(reference).append(space).append(MetaData.getForeignTable(field.getField()));
                    createTable.append(brOpen).append(MetaData.getForeignColumn(field.getField())).append(brClosed);
                }
            }
        }
        createTable.append(brClosed).append(semicolon);
        return createTable.toString();
    }
}
