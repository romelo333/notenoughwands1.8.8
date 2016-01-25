package mod.chiselsandbits.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Triggers C&B to create an instance of the class and call its callback method
 * on load.
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface ChiselsAndBitsAddon
{

}
