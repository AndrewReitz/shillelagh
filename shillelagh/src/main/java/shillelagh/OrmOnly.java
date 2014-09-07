package shillelagh;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * To signify to others that the constructor is only for Shillelagh use and is not to be invoked
 * TODO create a way to warn if direct usage is found (or allow privates and have shillelagh
 * use reflection? Also use as a progaurd rule?
 */
@Retention(SOURCE) @Target(CONSTRUCTOR)
public @interface OrmOnly {
}
