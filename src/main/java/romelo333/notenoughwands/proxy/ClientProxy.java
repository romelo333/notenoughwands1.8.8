package romelo333.notenoughwands.proxy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import romelo333.notenoughwands.KeyBindings;
import romelo333.notenoughwands.items.GenericWand;
import romelo333.notenoughwands.network.NetworkInit;
import romelo333.notenoughwands.network.PacketToggleMode;
import romelo333.notenoughwands.network.PacketToggleSubMode;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
//        MinecraftForge.EVENT_BUS.register(this);
//        McJtyLibClient.preInit(e);
    }

    @Override
    public void init() {
        super.init();
//        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
//        KeyBindings.init();
    }


    public static void renderWorldLastEvent() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ItemStack heldItem = mc.player.getMainHandStack();
        if (heldItem.isEmpty()) {
            return;
        }
        if (heldItem.getItem() instanceof GenericWand) {
            GenericWand genericWand = (GenericWand) heldItem.getItem();
            genericWand.renderOverlay(mc.player, heldItem, mc.getTickDelta());
        }
    }

    public static void checkKeys() {
        if (KeyBindings.wandModifier.method_1434()) {
            NetworkInit.sendToServer(new PacketToggleMode());
        } else if (KeyBindings.wandSubMode.method_1434()) {
            NetworkInit.sendToServer(new PacketToggleSubMode());
        }
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    public static int timer = 0;

//    @SubscribeEvent
//    public void onClientTick(TickEvent.ClientTickEvent event) {
//        if (Config.clientSideProtection < 0) {
//            return;
//        }
//
//        timer--;
//        if (timer > 0) {
//            return;
//        }
//        timer = Config.clientSideProtection;
//        NEWPacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlocksAroundPlayer());
//    }

//    @Override
//    public World getClientWorld() {
//        return MinecraftClient.getInstance().world;
//    }
//
//    @Override
//    public PlayerEntity getClientPlayer() {
//        return MinecraftClient.getInstance().player;
//    }
//
//    @Override
//    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
//        return MinecraftClient.getInstance().addScheduledTask(callableToSchedule);
//    }
//
//    @Override
//    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
//        return MinecraftClient.getInstance().addScheduledTask(runnableToSchedule);
//    }

}
