package romelo333.notenoughwands.Items;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.NotEnoughWands;

public class AdvancedWandCore extends Item {
    public AdvancedWandCore(String name) {
        setMaxStackSize(64);
        setUnlocalizedName(name);
        setCreativeTab(NotEnoughWands.tabNew);
        GameRegistry.registerItem(this, name);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel() {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(NotEnoughWands.MODID + ":" + getUnlocalizedName().substring(5), "inventory"));
    }

}
