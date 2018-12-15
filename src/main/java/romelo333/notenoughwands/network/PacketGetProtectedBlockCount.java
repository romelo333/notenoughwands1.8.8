package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import romelo333.notenoughwands.ProtectedBlocks;

public class PacketGetProtectedBlockCount /*implements IMessage*/ {
    private int id;

    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public PacketGetProtectedBlockCount() {
    }

    public PacketGetProtectedBlockCount(int id) {
        this.id = id;
    }

//    public static class Handler implements IMessageHandler<PacketGetProtectedBlockCount, IMessage> {
//        @Override
//        public IMessage onMessage(PacketGetProtectedBlockCount message, MessageContext ctx) {
//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
//            return null;
//        }
//
//        private void handle(PacketGetProtectedBlockCount message, MessageContext ctx) {
//            // @todo
//            PlayerEntityMP player = ctx.getServerHandler().player;
//            World world = player.getEntityWorld();
//
//            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
//            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(message.id));
//            NEWPacketHandler.INSTANCE.sendTo(msg, player);
//        }
//    }
}