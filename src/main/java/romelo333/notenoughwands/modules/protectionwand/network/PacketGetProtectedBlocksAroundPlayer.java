package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.network.NEWPacketHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketGetProtectedBlocksAroundPlayer {

    public void toBytes(FriendlyByteBuf buf) {
    }

    public PacketGetProtectedBlocksAroundPlayer() {
    }

    public PacketGetProtectedBlocksAroundPlayer(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            Level world = player.getCommandSenderWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            Map<ChunkPos, Set<BlockPos>> blocks = protectedBlocks.fetchProtectedBlocks(world, player.blockPosition());
            PacketReturnProtectedBlocksAroundPlayer msg = new PacketReturnProtectedBlocksAroundPlayer(blocks);
            NEWPacketHandler.INSTANCE.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}
