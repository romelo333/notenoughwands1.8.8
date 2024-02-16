package romelo333.notenoughwands.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import romelo333.notenoughwands.keys.KeyBindings;
import romelo333.notenoughwands.keys.KeyInputHandler;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;
import romelo333.notenoughwands.modules.protectionwand.network.PacketGetProtectedBlocksAroundPlayer;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.network.NEWPacketHandler;

public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientSetup());
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        KeyBindings.init();
        event.register(KeyBindings.wandModifier);
        event.register(KeyBindings.wandSubMode);
    }

    // @todo 1.20 correct event?
    @SubscribeEvent
    public void renderWorldLastEvent(RenderLevelStageEvent evt) {
        if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        ItemStack heldItem = p.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        if (heldItem.getItem() instanceof GenericWand) {
            GenericWand genericWand = (GenericWand) heldItem.getItem();
            genericWand.renderOverlay(evt, p, heldItem);
        }
    }

    public static int timer = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (ProtectionWandConfiguration.cachedClientSideProtection < 0) {
            return;
        }

        timer--;
        if (timer > 0) {
            return;
        }
        timer = ProtectionWandConfiguration.cachedClientSideProtection;
        if (Minecraft.getInstance().player != null) {
            NEWPacketHandler.sendToServer(new PacketGetProtectedBlocksAroundPlayer());
        }
    }
}
