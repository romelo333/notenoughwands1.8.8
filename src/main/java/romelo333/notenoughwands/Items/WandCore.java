package romelo333.notenoughwands.Items;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.NotEnoughWands;

public class WandCore extends Item {
    public WandCore (String name, String texture) {
        setMaxStackSize(64);
        setUnlocalizedName(name);
        setCreativeTab(NotEnoughWands.tabNew);
//        setTextureName(NotEnoughWands.MODID + ":" + texture);
        GameRegistry.registerItem(this, name);
    }

    public void registerModel(String name) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(NotEnoughWands.MODID + ":" + name, "inventory"));
    }
}
