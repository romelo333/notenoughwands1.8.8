package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import romelo333.notenoughwands.NotEnoughWands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketReturnProtectedBlocksAroundPlayer implements IMessage {
    private Map<ChunkPos, Set<BlockPos>> blocks;

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        blocks = new HashMap<ChunkPos, Set<BlockPos>> (size);
        for (int i = 0 ; i < size ; i++) {
            ChunkPos chunkpos = new ChunkPos(buf.readInt(), buf.readInt());

            int size2 = buf.readInt();
            Set<BlockPos> positions = new HashSet<>(size2);
            for (int j = 0 ; j < size2 ; j++) {
                positions.add(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
            }
            blocks.put(chunkpos, positions);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blocks.size());
        for (Map.Entry<ChunkPos, Set<BlockPos>> entry : blocks.entrySet()) {
            Set<BlockPos> positions = entry.getValue();
            buf.writeInt(entry.getKey().x);
            buf.writeInt(entry.getKey().z);
            buf.writeInt(positions.size());
            for (BlockPos block : positions) {
                buf.writeInt(block.getX());
                buf.writeInt(block.getY());
                buf.writeInt(block.getZ());
            }
        }
    }

    public Map<ChunkPos, Set<BlockPos>> getBlocks() {
        return blocks;
    }

    public PacketReturnProtectedBlocksAroundPlayer() {
    }

    public PacketReturnProtectedBlocksAroundPlayer(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnProtectedBlocksAroundPlayer(Map<ChunkPos, Set<BlockPos>> blocks) {
        this.blocks = blocks;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnProtectedBlocksAroundPlayerHelper.setProtectedBlocks(
                    NotEnoughWands.proxy.getClientWorld().provider.getDimension(), this);
        });
        ctx.setPacketHandled(true);
    }
}