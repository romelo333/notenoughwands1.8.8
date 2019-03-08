package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketGetProtectedBlocksAroundPlayer implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocksAroundPlayer() {
    }

    public PacketGetProtectedBlocksAroundPlayer(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetProtectedBlocksAroundPlayer(int chunkx, int chunkz) {
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP player = ctx.getSender();
            World world = player.getEntityWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            Map<ChunkPos, Set<BlockPos>> blocks = protectedBlocks.fetchProtectedBlocks(world, player.getPosition());
            PacketReturnProtectedBlocksAroundPlayer msg = new PacketReturnProtectedBlocksAroundPlayer(blocks);
            NEWPacketHandler.INSTANCE.sendTo(msg, player);
        });
        ctx.setPacketHandled(true);
    }
}
