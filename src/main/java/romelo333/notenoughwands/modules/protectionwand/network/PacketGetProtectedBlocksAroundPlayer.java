package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.network.NEWPacketHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketGetProtectedBlocksAroundPlayer {

    public void fromBytes(PacketBuffer buf) {
    }

    public void toBytes(PacketBuffer buf) {
    }

    public PacketGetProtectedBlocksAroundPlayer() {
    }

    public PacketGetProtectedBlocksAroundPlayer(PacketBuffer buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity player = ctx.getSender();
            World world = player.getEntityWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            Map<ChunkPos, Set<BlockPos>> blocks = protectedBlocks.fetchProtectedBlocks(world, player.getPosition());
            PacketReturnProtectedBlocksAroundPlayer msg = new PacketReturnProtectedBlocksAroundPlayer(blocks);
            NEWPacketHandler.INSTANCE.sendTo(msg, ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}
