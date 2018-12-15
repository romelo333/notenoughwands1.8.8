package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class PacketReturnProtectedBlocks /*implements IMessage*/ {
    private Set<BlockPos> blocks;
    private Set<BlockPos> childBlocks;

    public void fromBytes(ByteBuf buf) {
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

    public void toBytes(ByteBuf buf) {
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

    public PacketReturnProtectedBlocks(Set<BlockPos> blocks, Set<BlockPos> childBlocks) {
        this.blocks = blocks;
        this.childBlocks = childBlocks;
    }

    public static class Handler implements BiConsumer<PacketContext, PacketByteBuf> {

        @Override
        public void accept(PacketContext context, PacketByteBuf packetByteBuf) {
            PacketReturnProtectedBlocks packet = new PacketReturnProtectedBlocks();
            packet.fromBytes(packetByteBuf);
            context.getTaskQueue().execute(() -> handle(context, packet));
        }

        private void handle(PacketContext context, PacketReturnProtectedBlocks message) {
            ReturnProtectedBlocksHelper.setProtectedBlocks(message);
        }
    }
}