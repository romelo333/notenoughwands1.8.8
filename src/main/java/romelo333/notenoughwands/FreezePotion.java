package romelo333.notenoughwands;

import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;

public class FreezePotion extends Potion {
    public static FreezePotion freezePotion;

    Identifier icon = new Identifier(NotEnoughWands.MODID + ":textures/gui/effects/freeze.png");

    // @todo fabric
//    public FreezePotion() {
//        super(true, 0);
////        super(new ResourceLocation("freeze"), false, Color.BLUE.getRed());
//        // @todo
//    }

}
