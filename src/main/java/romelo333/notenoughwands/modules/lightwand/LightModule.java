package romelo333.notenoughwands.modules.lightwand;

import mcjty.lib.modules.IModule;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import romelo333.notenoughwands.modules.lightwand.blocks.LightBlock;
import romelo333.notenoughwands.modules.lightwand.blocks.LightTE;
import romelo333.notenoughwands.modules.lightwand.client.LightRenderer;
import romelo333.notenoughwands.modules.lightwand.items.IlluminationWand;
import romelo333.notenoughwands.setup.Registration;

import static romelo333.notenoughwands.setup.Registration.*;

public class LightModule implements IModule {

    public static final RegistryObject<Block> LIGHT = BLOCKS.register("light", LightBlock::new);
    public static final RegistryObject<Item> LIGHT_ITEM = ITEMS.register("light", () -> new BlockItem(LIGHT.get(), Registration.createStandardProperties()));
    public static final RegistryObject<BlockEntityType<LightTE>> TYPE_LIGHT = TILES.register("light", () -> BlockEntityType.Builder.of(LightTE::new, LIGHT.get()).build(null));

    public static final RegistryObject<Item> ILLUMINATION_WAND = ITEMS.register("illumination_wand", IlluminationWand::new);

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }
        event.addSprite(LightRenderer.LIGHT);
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(LightRenderer::register);
    }

    @Override
    public void initConfig() {
    }
}
