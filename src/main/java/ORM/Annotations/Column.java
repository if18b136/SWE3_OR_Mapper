package ORM.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to classify class fields as database columns.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * Primary key attribute for column-annotated class fields.
     *
     * @return  default false or true if set to it in annotation declaration.
     */
    boolean primary() default false;

    /**
     * Auto incrementation attribute for column-annotated class fields.
     *
     * @return  default false or true if set to it in annotation declaration.
     */
    boolean autoIncrement() default false;

    /**
     * Unique attribute for column-annotated class fields.
     *
     * @return  default false or true if set to it in annotation declaration.
     */
    boolean unique() default false;

    /**
     * Nullable attribute for column-annotated class fields.
     *
     * @return  default false or true if set to it in annotation declaration.
     */
    boolean nullable() default true;

    /**
     * Length attribute for column-annotated class fields.
     * Only makes sense to set if the column is a var type in database which length can be customized.
     *
     * @return  default (int) 255 or other length if set to it in annotation declaration.
     */
    int length() default 255;

    /**
     * Ignore attribute for column-annotated class fields.
     * Used for class fields that will have a relation to other objects in a class but don't need a foreign key entry on their own database table (1:1, 1:n).
     *
     * @return  default false or true if set to it in annotation declaration.
     */
    boolean ignore() default false;
}
