package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import romelo333.notenoughwands.ProtectedBlocks;

public class PacketGetProtectedBlockCount implements IMessage {
    private int id;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public PacketGetProtectedBlockCount() {
    }

    public PacketGetProtectedBlockCount(int id) {
        this.id = id;
    }

    public static class Handler implements IMessageHandler<PacketGetProtectedBlockCount, IMessage> {
        @Override
        public IMessage onMessage(PacketGetProtectedBlockCount message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetProtectedBlockCount message, MessageContext ctx) {
            // @todo
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.getEntityWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(message.id));
            NEWPacketHandler.INSTANCE.sendTo(msg, player);
        }
    }
}