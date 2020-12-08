package romelo333.notenoughwands.setup;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import romelo333.notenoughwands.keys.KeyBindings;
import romelo333.notenoughwands.keys.KeyInputHandler;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;
import romelo333.notenoughwands.modules.protectionwand.network.PacketGetProtectedBlocksAroundPlayer;
import romelo333.notenoughwands.network.NEWPacketHandler;

public class ClientProxy {

    // @todo 1.15 call me somewhere!
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        KeyBindings.init();
    }


    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
        // @todo 1.15
//        Minecraft mc = Minecraft.getMinecraft();
//        EntityPlayerSP p = mc.player;
//        ItemStack heldItem = p.getHeldItem(EnumHand.MAIN_HAND);
//        if (heldItem.isEmpty()) {
//            return;
//        }
//        if (heldItem.getItem() instanceof GenericWand) {
//            GenericWand genericWand = (GenericWand) heldItem.getItem();
//            genericWand.renderOverlay(evt, p, heldItem);
//        }
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
        NEWPacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlocksAroundPlayer());
    }
}
