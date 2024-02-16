package romelo333.notenoughwands.modules.lightwand;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import mcjty.lib.varia.ClientTools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import romelo333.notenoughwands.modules.lightwand.blocks.LightBlock;
import romelo333.notenoughwands.modules.lightwand.blocks.LightTE;
import romelo333.notenoughwands.modules.lightwand.client.LightRenderer;
import romelo333.notenoughwands.modules.lightwand.items.IlluminationWand;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.setup.Registration;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;
import static romelo333.notenoughwands.NotEnoughWands.tab;
import static romelo333.notenoughwands.setup.Registration.*;

public class LightModule implements IModule {

    public static final DeferredBlock<Block> LIGHT = BLOCKS.register("light", LightBlock::new);
    public static final DeferredItem<Item> LIGHT_ITEM = ITEMS.register("light", tab(() -> new BlockItem(LIGHT.get(), Registration.createStandardProperties())));
    public static final Supplier<BlockEntityType<LightTE>> TYPE_LIGHT = TILES.register("light", () -> BlockEntityType.Builder.of(LightTE::new, LIGHT.get()).build(null));

    public static final DeferredItem<Item> ILLUMINATION_WAND = ITEMS.register("illumination_wand", tab(IlluminationWand::new));

    public LightModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            ClientTools.onTextureStitch(bus, this::onTextureStitch);
        });
    }

    private List<ResourceLocation> onTextureStitch() {
        return Collections.singletonList(LightRenderer.LIGHT);
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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.blockBuilder(LIGHT)
                        .generatedItem("block/light")
                        .blockState(p -> p.singleTextureBlock(LIGHT.get(), BLOCK_FOLDER + "/light", "block/empty")),
                Dob.itemBuilder(ILLUMINATION_WAND)
                        .handheldItem("item/illumination_wand")
                        .shaped(builder -> builder
                                        .define('x', Items.GLOWSTONE_DUST)
                                        .define('w', WandsModule.WAND_CORE.get())
                                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                                "xx ", "xw ", "  w"
                        )
        );
    }
}
