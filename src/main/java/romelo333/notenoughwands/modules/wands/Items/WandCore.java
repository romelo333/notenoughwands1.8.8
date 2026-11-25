package romelo333.notenoughwands.modules.wands.Items;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item;

public class WandCore extends Item {
    public WandCore(Properties properties) {
        super(properties
                .stacksTo(64)
        );
    }
}
