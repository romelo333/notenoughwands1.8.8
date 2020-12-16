package romelo333.notenoughwands.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
        KeyBindings.init();
    }


    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity p = mc.player;
        ItemStack heldItem = p.getHeldItem(Hand.MAIN_HAND);
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
        if (ProtectionWandConfiguration.clientSideProtection.get() < 0) {
            return;
        }

        timer--;
        if (timer > 0) {
            return;
        }
        timer = ProtectionWandConfiguration.clientSideProtection.get();
        if (Minecraft.getInstance().player != null) {
            NEWPacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlocksAroundPlayer());
        }
    }
}
