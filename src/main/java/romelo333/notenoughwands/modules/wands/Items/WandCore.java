package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.world.item.Item;
import romelo333.notenoughwands.NotEnoughWands;

import net.minecraft.world.item.Item.Properties;

public class WandCore extends Item {
    public WandCore () {
        super(NotEnoughWands.setup.defaultProperties()
                .stacksTo(64)
        );
    }
}
