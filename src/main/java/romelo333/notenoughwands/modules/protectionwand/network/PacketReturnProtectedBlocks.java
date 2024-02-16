package romelo333.notenoughwands.modules.protectionwand.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import romelo333.notenoughwands.NotEnoughWands;

import java.util.HashSet;
import java.util.Set;

public record PacketReturnProtectedBlocks(Set<BlockPos> blocks, Set<BlockPos> childBlocks) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NotEnoughWands.MODID, "returnprotectedblocks");

    public static PacketReturnProtectedBlocks create(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Set<BlockPos> blocks = new HashSet<>(size);
        for (int i = 0 ; i < size ; i++) {
            blocks.add(buf.readBlockPos());
        }
        size = buf.readInt();
        Set<BlockPos> childBlocks = new HashSet<>(size);
        for (int i = 0 ; i < size ; i++) {
            childBlocks.add(buf.readBlockPos());
        }
        return new PacketReturnProtectedBlocks(blocks, childBlocks);
    }

    public static PacketReturnProtectedBlocks create(Set<BlockPos> blocks, Set<BlockPos> childBlocks) {
        return new PacketReturnProtectedBlocks(blocks, childBlocks);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(blocks.size());
        for (BlockPos block : blocks) {
            buf.writeBlockPos(block);
        }
        buf.writeInt(childBlocks.size());
        for (BlockPos block : childBlocks) {
            buf.writeBlockPos(block);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public Set<BlockPos> getBlocks() {
        return blocks;
    }

    public Set<BlockPos> getChildBlocks() {
        return childBlocks;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ReturnProtectedBlocksHelper.setProtectedBlocks(this);
        });
    }
}