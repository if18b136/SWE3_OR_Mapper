package ORM.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MtoN {
    /**
     * table name for m:n-annotated class fields.
     * Needs to be the same for every corresponding class.
     *
     * @return  Table name of the m:n table in database.
     */
    String table() default "";

    Class<?> correspondingClass();

}
