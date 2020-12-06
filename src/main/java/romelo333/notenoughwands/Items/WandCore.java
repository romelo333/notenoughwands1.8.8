package romelo333.notenoughwands.Items;


import net.minecraft.item.Item;
import romelo333.notenoughwands.NotEnoughWands;

public class WandCore extends Item {
    public WandCore () {
        super(new Properties().group(NotEnoughWands.setup.getTab())
                .maxStackSize(64)
        );
    }
}
