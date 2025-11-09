package toolkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used solely for aesthetic purposes
 * to mark JavaFX components. It does not alter functionality
 * or provide additional behavior.
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface Component {
}
