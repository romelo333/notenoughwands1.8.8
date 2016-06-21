package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PacketGetProtectedBlocksAroundPlayer implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocksAroundPlayer() {
    }

    public PacketGetProtectedBlocksAroundPlayer(int chunkx, int chunkz) {
    }

    public static class Handler implements IMessageHandler<PacketGetProtectedBlocksAroundPlayer, IMessage> {
        @Override
        public IMessage onMessage(PacketGetProtectedBlocksAroundPlayer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetProtectedBlocksAroundPlayer message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            World world = player.worldObj;

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            Map<ChunkPos, Set<BlockPos>> blocks = protectedBlocks.fetchProtectedBlocks(world, player.getPosition());
            PacketReturnProtectedBlocksAroundPlayer msg = new PacketReturnProtectedBlocksAroundPlayer(blocks);
            PacketHandler.INSTANCE.sendTo(msg, player);
        }
    }
}
