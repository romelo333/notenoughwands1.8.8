package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import romelo333.notenoughwands.Items.GenericWand;

public class PacketToggleMode /*implements IMessage*/ {

//    @Override
    public void fromBytes(ByteBuf buf) {
    }

//    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleMode() {
    }

//    public static class Handler implements IMessageHandler<PacketToggleMode, IMessage> {
//        @Override
//        public IMessage onMessage(PacketToggleMode message, MessageContext ctx) {
//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
//            return null;
//        }
//
//        private void handle(PacketToggleMode message, MessageContext ctx) {
//            // @todo
//            PlayerEntityMP playerEntity = ctx.getServerHandler().player;
//            ItemStack heldItem = playerEntity.getHeldItem(EnumHand.MAIN_HAND);
//            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
//                GenericWand genericWand = (GenericWand) (heldItem.getItem());
//                genericWand.toggleMode(playerEntity, heldItem);
//            }
//        }
//    }
}
