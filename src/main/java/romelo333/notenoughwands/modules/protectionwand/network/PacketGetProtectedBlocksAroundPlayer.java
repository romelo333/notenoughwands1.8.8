package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.network.NEWPacketHandler;

import java.util.Map;
import java.util.Set;

public record PacketGetProtectedBlocksAroundPlayer() implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NotEnoughWands.MODID, "getprotectedblocksaroundplayer");

    public static PacketGetProtectedBlocksAroundPlayer create(FriendlyByteBuf buf) {
        return new PacketGetProtectedBlocksAroundPlayer();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                Level world = player.getCommandSenderWorld();

                ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
                Map<ChunkPos, Set<BlockPos>> blocks = protectedBlocks.fetchProtectedBlocks(world, player.blockPosition());
                PacketReturnProtectedBlocksAroundPlayer msg = new PacketReturnProtectedBlocksAroundPlayer(blocks);
                NEWPacketHandler.sendToPlayer(msg, player);
            });
        });
    }
}
