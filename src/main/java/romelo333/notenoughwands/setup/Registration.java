package romelo333.notenoughwands.setup;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import romelo333.notenoughwands.NotEnoughWands;

import static romelo333.notenoughwands.NotEnoughWands.MODID;

public class Registration {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(NotEnoughWands.setup.getTab());
    }

    // @todo 1.15
//    @SubscribeEvent
//    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
//        ModSounds.init(event.getRegistry());
//    }
//
//    @SubscribeEvent
//    public static void registerBlocks(RegistryEvent.Register<Block> event) {
//        ModBlocks.lightBlock = new LightBlock();
//        event.getRegistry().register(ModBlocks.lightBlock);
//        GameRegistry.registerTileEntity(LightTE.class, "LightTileEntity");
//    }
//
//    @SubscribeEvent
//    public static void registerItems(RegistryEvent.Register<Item> event) {
//        event.getRegistry().register(ModItems.wandCore);
//        event.getRegistry().register(ModItems.advancedWandCore);
//
//        for (GenericWand wand : GenericWand.getWands()) {
//            event.getRegistry().register(wand);
//        }
//    }

}
