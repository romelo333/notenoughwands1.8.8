package romelo333.notenoughwands.proxy;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import romelo333.notenoughwands.KeyBindings;
import romelo333.notenoughwands.KeyInputHandler;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.ModRenderers;

//import net.minecraft.client.entity.EntityClientPlayerMP;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ModRenderers.init();
        ModItems.initModels();
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(new KeyInputHandler());
        KeyBindings.init();
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
//        Minecraft mc = Minecraft.getMinecraft();
//        EntityClientPlayerMP p = mc.thePlayer;
//        ItemStack heldItem = p.getHeldItem();
//        if (heldItem == null) {
//            return;
//        }
//        if (heldItem.getItem() instanceof GenericWand) {
//            GenericWand genericWand = (GenericWand) heldItem.getItem();
//            genericWand.renderOverlay(evt, p, heldItem);
//        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
