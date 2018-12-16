package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import romelo333.notenoughwands.NotEnoughWands;

import java.util.HashSet;
import java.util.Set;

public class PacketReturnProtectedBlocks implements IPacket {

    public static final Identifier RETURN_PROTECTED_BLOCKS = new Identifier(NotEnoughWands.MODID, "return_protected_blocks");

    private Set<BlockPos> blocks;
    private Set<BlockPos> childBlocks;

    @Override
    public Identifier getId() {
        return RETURN_PROTECTED_BLOCKS;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        blocks = new HashSet<>(size);
        for (int i = 0 ; i < size ; i++) {
            blocks.add(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
        }
        size = buf.readInt();
        childBlocks = new HashSet<>(size);
        for (int i = 0 ; i < size ; i++) {
            childBlocks.add(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
        }
    }

    @Override
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

    public static class Handler extends MessageHandler<PacketReturnProtectedBlocks> {

        @Override
        protected PacketReturnProtectedBlocks createPacket() {
            return new PacketReturnProtectedBlocks();
        }

        @Override
        public void handle(PacketContext context, PacketReturnProtectedBlocks message) {
            ReturnProtectedBlocksHelper.setProtectedBlocks(message);
        }
    }
}