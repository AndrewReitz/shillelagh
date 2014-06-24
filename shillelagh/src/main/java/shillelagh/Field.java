package shillelagh;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/** All Database fields must be marked with this annotation. */
@Retention(SOURCE)
@Target(FIELD)
public @interface Field {
}
