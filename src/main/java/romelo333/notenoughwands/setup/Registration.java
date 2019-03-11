package romelo333.notenoughwands.setup;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Items.GenericWand;
import romelo333.notenoughwands.ModBlocks;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.ModSounds;
import romelo333.notenoughwands.blocks.LightBlock;
import romelo333.notenoughwands.blocks.LightTE;

@Mod.EventBusSubscriber
public class Registration {

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.init(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.lightBlock = new LightBlock();
        event.getRegistry().register(ModBlocks.lightBlock);
        GameRegistry.registerTileEntity(LightTE.class, "LightTileEntity");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModItems.wandCore);
        event.getRegistry().register(ModItems.advancedWandCore);

        for (GenericWand wand : GenericWand.getWands()) {
            event.getRegistry().register(wand);
        }
    }

}
