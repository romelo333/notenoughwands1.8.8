package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import romelo333.notenoughwands.Items.ProtectionWand;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class PacketGetProtectedBlocks /*implements IMessage */ {
    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocks() {
    }

    public static class Handler implements BiConsumer<PacketContext, PacketByteBuf> {

        @Override
        public void accept(PacketContext context, PacketByteBuf packetByteBuf) {
            PacketGetProtectedBlocks packet = new PacketGetProtectedBlocks();
            packet.fromBytes(packetByteBuf);
            context.getTaskQueue().execute(() -> handle(context, packet));
        }

        private void handle(PacketContext context, PacketGetProtectedBlocks message) {
            PlayerEntity player = context.getPlayer();
            World world = player.getEntityWorld();

            ItemStack heldItem = player.getMainHandStack();
            if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ProtectionWand)) {
                // Cannot happen normally
                return;
            }
            ProtectionWand protectionWand = (ProtectionWand) heldItem.getItem();
            int id = protectionWand.getId(heldItem);

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            Set<BlockPos> blocks = new HashSet<>();
            protectedBlocks.fetchProtectedBlocks(blocks, world, (int)player.x, (int)player.y, (int)player.z, protectionWand.blockShowRadius, id);
            Set<BlockPos> childBlocks = new HashSet<>();
            if (id == -1) {
                // Master wand:
                protectedBlocks.fetchProtectedBlocks(childBlocks, world, (int)player.x, (int)player.y, (int)player.z, protectionWand.blockShowRadius, -2);
            }
            PacketReturnProtectedBlocks msg = new PacketReturnProtectedBlocks(blocks, childBlocks);
            NetworkInit.returnProtectedBlocks(msg, (ServerPlayerEntity) player);
        }
    }
}
