package romelo333.notenoughwands.Items;


import net.minecraft.item.Item;
import romelo333.notenoughwands.NotEnoughWands;

public class AdvancedWandCore extends Item {
    public AdvancedWandCore() {
        super(new Properties().group(NotEnoughWands.setup.getTab())
                .maxStackSize(64)
        );
    }
}
