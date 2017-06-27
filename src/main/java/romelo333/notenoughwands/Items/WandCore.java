package romelo333.notenoughwands.Items;


import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.NotEnoughWands;

public class WandCore extends Item {
    public WandCore (String name) {
        setMaxStackSize(64);
        setUnlocalizedName(NotEnoughWands.MODID + "." + name);
        setRegistryName(name);
        setCreativeTab(NotEnoughWands.tabNew);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
