package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;

// @todo fabric
public class PacketToggleSubMode /*implements IMessage*/ {

    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleSubMode() {
    }

//    public static class Handler implements IMessageHandler<PacketToggleSubMode, IMessage> {
//        @Override
//        public IMessage onMessage(PacketToggleSubMode message, MessageContext ctx) {
//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
//            return null;
//        }
//
//        private void handle(PacketToggleSubMode message, MessageContext ctx) {
//
//            // @todo
//            PlayerEntityMP playerEntity = ctx.getServerHandler().player;
//            ItemStack heldItem = playerEntity.getHeldItem(EnumHand.MAIN_HAND);
//            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
//                GenericWand genericWand = (GenericWand) (heldItem.getItem());
//                genericWand.toggleSubMode(playerEntity, heldItem);
//            }
//        }
//    }
}
