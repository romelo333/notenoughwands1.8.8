package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.items.ProtectionWand;

import java.util.HashSet;
import java.util.Set;

public class PacketGetProtectedBlocks implements IPacket {


    public static final Identifier GET_PROTECTED_BLOCKS = new Identifier(NotEnoughWands.MODID, "get_protected_blocks");

    @Override
    public Identifier getId() {
        return GET_PROTECTED_BLOCKS;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocks() {
    }

    public static class Handler extends MessageHandler<PacketGetProtectedBlocks> {

        @Override
        protected PacketGetProtectedBlocks createPacket() {
            return new PacketGetProtectedBlocks();
        }

        @Override
        public void handle(PacketContext context, PacketGetProtectedBlocks message) {
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
            NetworkInit.sendToClient(msg, (ServerPlayerEntity) player);
        }
    }
}
