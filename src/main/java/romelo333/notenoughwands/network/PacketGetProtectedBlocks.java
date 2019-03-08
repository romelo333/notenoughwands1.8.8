package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import romelo333.notenoughwands.Items.ProtectionWand;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketGetProtectedBlocks implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocks() {
    }

    public PacketGetProtectedBlocks(ByteBuf buf) {
        fromBytes(buf);
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP player = ctx.getSender();
            World world = player.getEntityWorld();

            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ProtectionWand)) {
                // Cannot happen normally
                return;
            }
            ProtectionWand protectionWand = (ProtectionWand) heldItem.getItem();
            int id = protectionWand.getId(heldItem);

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            Set<BlockPos> blocks = new HashSet<>();
            protectedBlocks.fetchProtectedBlocks(blocks, world, (int)player.posX, (int)player.posY, (int)player.posZ, protectionWand.blockShowRadius, id);
            Set<BlockPos> childBlocks = new HashSet<>();
            if (id == -1) {
                // Master wand:
                protectedBlocks.fetchProtectedBlocks(childBlocks, world, (int)player.posX, (int)player.posY, (int)player.posZ, protectionWand.blockShowRadius, -2);
            }
            PacketReturnProtectedBlocks msg = new PacketReturnProtectedBlocks(blocks, childBlocks);
            NEWPacketHandler.INSTANCE.sendTo(msg, player);
        });
        ctx.setPacketHandled(true);
    }
}
