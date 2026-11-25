package romelo333.notenoughwands.setup;

import com.mojang.serialization.Codec;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.setup.DeferredRegister;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.wands.WandsModule;

import java.util.function.Supplier;

import static romelo333.notenoughwands.NotEnoughWands.MODID;

public class Registration {

    public static final DeferredItems ITEMS = DeferredItems.create(MODID);
    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static void register() {
        BLOCKS.register();
        ITEMS.register();
        TILES.register();
        SOUNDS.register();
        TABS.register();
        COMPONENTS.register();
    }

    public static final Supplier<DataComponentType<Integer>> ENERGY_COMPONENT = COMPONENTS.register("energy", () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build());


    public static Item.Properties createStandardProperties() {
        return NotEnoughWands.setup.defaultProperties();
    }

    public static Supplier<CreativeModeTab> TAB = TABS.register("notenoughwands", () -> FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(WandsModule.WAND_CORE.get()))
            .displayItems((featureFlags, output) -> {
                NotEnoughWands.setup.populateTab(output);
            })
            .build());
}
