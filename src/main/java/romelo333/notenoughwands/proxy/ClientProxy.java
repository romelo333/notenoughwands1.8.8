package romelo333.notenoughwands.proxy;

import mcjty.lib.McJtyLibClient;
import mcjty.lib.setup.DefaultClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import romelo333.notenoughwands.*;
import romelo333.notenoughwands.Items.GenericWand;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.network.PacketGetProtectedBlocksAroundPlayer;

public class ClientProxy extends DefaultClientProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(this);
        McJtyLibClient.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        KeyBindings.init();
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModRenderers.init();
        ModItems.initModels();
    }


    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;
        ItemStack heldItem = p.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem.isEmpty()) {
            return;
        }
        if (heldItem.getItem() instanceof GenericWand) {
            GenericWand genericWand = (GenericWand) heldItem.getItem();
            genericWand.renderOverlay(evt, p, heldItem);
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    public static int timer = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Config.clientSideProtection < 0) {
            return;
        }

        timer--;
        if (timer > 0) {
            return;
        }
        timer = Config.clientSideProtection;
        NEWPacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlocksAroundPlayer());
    }
}
