package romelo333.notenoughwands.modules.lightwand;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredItem;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import romelo333.notenoughwands.modules.lightwand.blocks.LightBlock;
import romelo333.notenoughwands.modules.lightwand.blocks.LightTE;
import romelo333.notenoughwands.modules.lightwand.client.LightRenderer;
import romelo333.notenoughwands.modules.lightwand.items.IlluminationWand;
import romelo333.notenoughwands.modules.wands.WandsModule;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static romelo333.notenoughwands.NotEnoughWands.tab;
import static romelo333.notenoughwands.setup.Registration.*;
import static romelo333.notenoughwands.setup.Registration.TILES;

public class LightModule implements IModule {

    public static final DeferredBlock<Block> LIGHT = BLOCKS.register("light", LightBlock::new);
    public static final DeferredItem<Item> LIGHT_ITEM = ITEMS.register("light", tab(properties -> new BlockItem(LIGHT.get(), properties)));
    public static final Supplier<BlockEntityType<LightTE>> TYPE_LIGHT = TILES.register("light", () -> FabricBlockEntityTypeBuilder.create(LightTE::new, LIGHT.get()).build());

    public static final DeferredItem<Item> ILLUMINATION_WAND = ITEMS.register("illumination_wand", tab(IlluminationWand::new));

    @Override
    public void init() {

    }

    @Override
    public void initClient() {
        LightRenderer.register();
    }

    @Override
    public void initConfig() {
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(LIGHT)
                        .parentedItem("block/light")
                        .blockState(p -> p.singleTextureBlock(LIGHT.get(), "block/light", "block/empty")),
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
