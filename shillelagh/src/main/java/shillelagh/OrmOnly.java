package shillelagh;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/** To signify to others that the constructor is only for Shillelagh use and is not to be invoked */
@Retention(SOURCE) @Target(CONSTRUCTOR)
public @interface OrmOnly {
}
