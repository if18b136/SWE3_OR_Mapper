package ORM.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to classify a class as a database table.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * Database table name of the class.
     *
     * @return  Table name of the table-annotated class.
     */
    String name() default "";

    /**
     * Class array of child classes of this class.
     *
     * @return  Class array of child classes of this table-annotated class.
     */
    Class<?>[] subclasses() default Object.class;   // TODO technically currently not needed?
}
