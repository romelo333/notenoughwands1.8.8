package romelo333.notenoughwands;


import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.blocks.LightBlock;
import romelo333.notenoughwands.blocks.LightTE;

public class ModBlocks {
    public static LightBlock lightBlock;

    public static void init() {
        lightBlock = new LightBlock();
        GameRegistry.registerBlock(lightBlock, "lightBlock");
        GameRegistry.registerTileEntity(LightTE.class, "LightTileEntity");
    }
}
