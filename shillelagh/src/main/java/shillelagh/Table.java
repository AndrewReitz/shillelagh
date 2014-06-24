package shillelagh;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * To be placed on class that will be inserted into the database.
 *
 * @see shillelagh.Id
 * @see shillelagh.Field
 */
@Retention(CLASS) @Target(TYPE)
public @interface Table {
}
