package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.Set;

public class PacketGetProtectedBlocksAroundPlayer /*implements IMessage*/ {

    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocksAroundPlayer() {
    }

    public PacketGetProtectedBlocksAroundPlayer(int chunkx, int chunkz) {
    }

//    public static class Handler implements IMessageHandler<PacketGetProtectedBlocksAroundPlayer, IMessage> {
//        @Override
//        public IMessage onMessage(PacketGetProtectedBlocksAroundPlayer message, MessageContext ctx) {
//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
//            return null;
//        }
//
//        private void handle(PacketGetProtectedBlocksAroundPlayer message, MessageContext ctx) {
//            // @todo
//            PlayerEntityMP player = ctx.getServerHandler().player;
//            World world = player.getEntityWorld();
//
//            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
//            Map<ChunkPos, Set<BlockPos>> blocks = protectedBlocks.fetchProtectedBlocks(world, player.getPosition());
//            PacketReturnProtectedBlocksAroundPlayer msg = new PacketReturnProtectedBlocksAroundPlayer(blocks);
//            NEWPacketHandler.INSTANCE.sendTo(msg, player);
//        }
//    }
}
