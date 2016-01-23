package mod.chiselsandbits.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Put this on the block, or use the IMC,
 *
 * FMLInterModComms.sendMessage( "chiselsandbits", "ignoreblocklogic",
 * "myBlockName" );
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface IgnoreBlockLogic
{

}
