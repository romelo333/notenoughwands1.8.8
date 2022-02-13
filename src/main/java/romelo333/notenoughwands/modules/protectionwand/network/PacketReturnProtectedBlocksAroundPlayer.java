package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketReturnProtectedBlocksAroundPlayer {
    private Map<ChunkPos, Set<BlockPos>> blocks;

    public void fromBytes(FriendlyByteBuf buf) {
        int size = buf.readInt();
        blocks = new HashMap<>(size);
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

    public void toBytes(FriendlyByteBuf buf) {
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

    public PacketReturnProtectedBlocksAroundPlayer(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnProtectedBlocksAroundPlayer(Map<ChunkPos, Set<BlockPos>> blocks) {
        this.blocks = blocks;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnProtectedBlocksAroundPlayerHelper.setProtectedBlocks(Level.OVERWORLD, this);
                    //McJtyLib.proxy.getClientWorld().dimension(), this); // @todo 1.15 no need for proxy here!
        });
        ctx.setPacketHandled(true);
    }
}