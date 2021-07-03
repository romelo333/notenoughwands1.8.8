package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.item.Item;
import romelo333.notenoughwands.NotEnoughWands;

import net.minecraft.item.Item.Properties;

public class AdvancedWandCore extends Item {
    public AdvancedWandCore() {
        super(new Properties().tab(NotEnoughWands.setup.getTab())
                .stacksTo(64)
        );
    }
}
