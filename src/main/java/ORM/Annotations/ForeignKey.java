package ORM.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to classify class fields as foreign key relations in database.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {

    /**
     * Foreign table name for foreignKey-annotated class fields.
     *
     * @return  Table name of the foreign key in database.
     */
    String table() default "";

    /**
     * Foreign column name for foreignKey-annotated class fields.
     *
     * @return  Column name of the foreign key in database.
     */
    String column() default "";

    /**
     * Table name of the other source table for a m:n relation table for foreignKey-annotated class fields.
     *
     * @return  Table name of the other m:n table.
     */
    String foreignColumn() default "";
}
