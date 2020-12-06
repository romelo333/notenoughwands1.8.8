package romelo333.notenoughwands.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import romelo333.notenoughwands.Items.ProtectionWand;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketGetProtectedBlocks {
    public void fromBytes(PacketBuffer buf) {
    }

    public void toBytes(PacketBuffer buf) {
    }

    public PacketGetProtectedBlocks() {
    }

    public PacketGetProtectedBlocks(PacketBuffer buf) {
        fromBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity player = ctx.getSender();
            World world = player.getEntityWorld();

            ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
            if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ProtectionWand)) {
                // Cannot happen normally
                return;
            }
            ProtectionWand protectionWand = (ProtectionWand) heldItem.getItem();
            int id = protectionWand.getId(heldItem);

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            Set<BlockPos> blocks = new HashSet<>();
            protectedBlocks.fetchProtectedBlocks(blocks, world, (int)player.getPosX(), (int)player.getPosY(), (int)player.getPosZ(), protectionWand.blockShowRadius, id);
            Set<BlockPos> childBlocks = new HashSet<>();
            if (id == -1) {
                // Master wand:
                protectedBlocks.fetchProtectedBlocks(childBlocks, world, (int)player.getPosX(), (int)player.getPosY(), (int)player.getPosZ(), protectionWand.blockShowRadius, -2);
            }
            PacketReturnProtectedBlocks msg = new PacketReturnProtectedBlocks(blocks, childBlocks);
            NEWPacketHandler.INSTANCE.sendTo(msg, ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}
