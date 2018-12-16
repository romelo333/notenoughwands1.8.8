package romelo333.notenoughwands.items;


import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import romelo333.notenoughwands.NotEnoughWands;

public class AdvancedWandCore extends Item {
    public AdvancedWandCore(String name) {
        super(new Settings().itemGroup(ItemGroup.TOOLS));
        Registry.ITEM.register(new Identifier(NotEnoughWands.MODID, name), this);
    }

}
