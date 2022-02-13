package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketReturnProtectedBlocks {
    private Set<BlockPos> blocks;
    private Set<BlockPos> childBlocks;

    public void fromBytes(FriendlyByteBuf buf) {
        int size = buf.readInt();
        blocks = new HashSet<BlockPos>(size);
        for (int i = 0 ; i < size ; i++) {
            blocks.add(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
        }
        size = buf.readInt();
        childBlocks = new HashSet<BlockPos>(size);
        for (int i = 0 ; i < size ; i++) {
            childBlocks.add(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(blocks.size());
        for (BlockPos block : blocks) {
            buf.writeInt(block.getX());
            buf.writeInt(block.getY());
            buf.writeInt(block.getZ());
        }
        buf.writeInt(childBlocks.size());
        for (BlockPos block : childBlocks) {
            buf.writeInt(block.getX());
            buf.writeInt(block.getY());
            buf.writeInt(block.getZ());
        }
    }


    public Set<BlockPos> getBlocks() {
        return blocks;
    }

    public Set<BlockPos> getChildBlocks() {
        return childBlocks;
    }

    public PacketReturnProtectedBlocks() {
    }

    public PacketReturnProtectedBlocks(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnProtectedBlocks(Set<BlockPos> blocks, Set<BlockPos> childBlocks) {
        this.blocks = blocks;
        this.childBlocks = childBlocks;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnProtectedBlocksHelper.setProtectedBlocks(this);
        });
        ctx.setPacketHandled(true);
    }
}