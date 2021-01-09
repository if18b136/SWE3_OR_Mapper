package ORM.Queries;

import ORM.Annotations.Column;
import ORM.Annotations.ForeignKey;
import ORM.Base.Entity;
import ORM.Base.Field;
import ORM.Manager;
import ORM.MetaData;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL query class for create table operations.
 */
public class CreateTableQuery implements QueryLanguage {
    /**
     * CREATE TABLE sql string.
     */
    private final String operation = "CREATE TABLE";
    /**
     * Full query.
     */
    String query;

    /**
     * Constructs a table creation query from a entity object.
     *
     * @param entity    Source for the query.
     * @return  A legal sql create table query.
     */
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
                                    createTable.append(MetaData.parseType(foreignField.getType().getName(),((Column) annotation).length()));
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                }
                            } else {    // Set to foreign key but is not a custom class - means user better made sure to assign the same type to both fields
                                createTable.append(MetaData.parseType(field.getField().getType().getName(),((Column) annotation).length()));
                            }
                        } else {
                            createTable.append(MetaData.parseType(field.getField().getType().getName(),((Column) annotation).length()));
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

                    if(field.isNullable() && !field.isPrimary()) {    //if nullable non-pk let the db set fk values to null
                        createTable.append(space).append(odsn).append(space).append(ouc);
                    } else {    // if not nullable the entry should not be deleted - entry that reference the fk entity need to be deleted first.
                        createTable.append(space).append(odr).append(space).append(ouc);
                    }
                }
            }
        }
        createTable.append(brClosed).append(semicolon);
        return createTable.toString();
    }

    public String buildManyQuery(String tableName, Entity... entities) {
        StringBuilder createTable = new StringBuilder();
        createTable.append(operation).append(space).append(tableName).append(space).append(brOpen);    // "CREATE TABLE" + " " + "--table Name--" + " " + "("
        StringBuilder fk = new StringBuilder();
        boolean first = true;

        for (Entity entity : entities) {    // traverse through all entities
            for(Field field : entity.getPrimaryFields()) {  // traverse through all primary key fields
                Annotation[] annotations = field.getField().getDeclaredAnnotations();
                for (Annotation annotation : annotations) { // traverse through all annotations to get the fk+pk fields
                    if(annotation instanceof Column) {  // only search for fk needed because getPrimaryFields() made sure there are only pk Fields getting traversed
                        if(!first) { createTable.append(comma); } // every additional entry needs a ", " at the beginning}
                        createTable.append(space).append(entity.getTableName()).append("_").append(field.getColumnName());        // "--column Name--" + " "
                        if(field.isForeign()) { // fk+pk fields need the type of their original entity
                            String foreignColumnName = field.getForeignColumn();
                            Entity foreignEntity = Manager.getEntityIfExists(field.getFieldType());
                            if(foreignEntity != null) {
                                Class<?> foreignClass = foreignEntity.getEntityClass();
                                try {
                                    java.lang.reflect.Field foreignField = foreignClass.getDeclaredField(foreignColumnName);    // get the reflection field of the foreign key
                                    createTable.append(space).append(MetaData.parseType(foreignField.getType().getName(),((Column) annotation).length()));
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                }
                            } else {    // Set to foreign key but is not a custom class - means user better made sure to assign the same type to both fields
                                createTable.append(space).append(MetaData.parseType(field.getField().getType().getName(),((Column) annotation).length()));
                            }
                        } else {
                            createTable.append(space).append(MetaData.parseType(field.getField().getType().getName(),((Column) annotation).length()));
                        }
                        // not needed here because every entry needs a foreign key addressing the original table
//                    } else if (annotation instanceof ForeignKey) {
//                        createTable.append(comma).append(space).append(foreign).append(space).append(brOpen);
//                        createTable.append(field.getColumnName()).append(brClosed);
//                        createTable.append(space).append(reference).append(space).append(MetaData.getForeignTable(field.getField()));
//                        createTable.append(brOpen).append(MetaData.getForeignColumn(field.getField())).append(brClosed);
                        fk.append(comma).append(space).append(foreign).append(space).append(brOpen);
                        fk.append(entity.getTableName()).append("_").append(field.getColumnName()).append(brClosed);   // appending the new colName
                        fk.append(space).append(reference).append(space).append(entity.getTableName());                // reference the entity table name
                        fk.append(brOpen).append(field.getColumnName()).append(brClosed);
                        // append update and delete conditions
                        if(field.isNullable()) {    //if nullable let the db set fk values to null
                            fk.append(space).append(odsn).append(space).append(ouc);
                        } else {    // if not nullable delete the whole entry - is not needed anymore if one of the m:n instances is deleted.
                            fk.append(space).append(odc).append(space).append(ouc);
                        }
                    }
                    first = false;
                }
            }

        }
        createTable.append(fk);
        createTable.append(brClosed).append(semicolon);
        return createTable.toString();
    }

}
