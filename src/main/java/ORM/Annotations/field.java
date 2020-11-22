package ORM.Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface field {
    /** Field name. */
    public String fieldName() default "";

    /** Column name. */
    public String columnName() default "";

    /** Column type. */
    public Class columnType() default Void.class;
}
