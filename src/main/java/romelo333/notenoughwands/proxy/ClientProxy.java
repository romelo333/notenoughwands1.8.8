package romelo333.notenoughwands.proxy;

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


// @todo fabric
//    @SubscribeEvent
//    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
//        Minecraft mc = MinecraftClient.getInstance();
//        PlayerEntitySP p = mc.player;
//        ItemStack heldItem = p.getHeldItem(EnumHand.MAIN_HAND);
//        if (heldItem.isEmpty()) {
//            return;
//        }
//        if (heldItem.getItem() instanceof GenericWand) {
//            GenericWand genericWand = (GenericWand) heldItem.getItem();
//            genericWand.renderOverlay(evt, p, heldItem);
//        }
//    }

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
