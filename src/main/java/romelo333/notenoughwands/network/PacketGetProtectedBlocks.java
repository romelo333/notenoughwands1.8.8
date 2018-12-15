package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import romelo333.notenoughwands.Items.ProtectionWand;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.HashSet;
import java.util.Set;

public class PacketGetProtectedBlocks /*implements IMessage */ {
    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocks() {
    }

//    public static class Handler implements IMessageHandler<PacketGetProtectedBlocks, IMessage> {
//        @Override
//        public IMessage onMessage(PacketGetProtectedBlocks message, MessageContext ctx) {
//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
//            return null;
//        }
//
//        private void handle(PacketGetProtectedBlocks message, MessageContext ctx) {
//            // @todo (compatlayer?)
//            PlayerEntityMP player = ctx.getServerHandler().player;
//            World world = player.getEntityWorld();
//
//            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
//            if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ProtectionWand)) {
//                // Cannot happen normally
//                return;
//            }
//            ProtectionWand protectionWand = (ProtectionWand) heldItem.getItem();
//            int id = protectionWand.getId(heldItem);
//
//            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
//            Set<BlockPos> blocks = new HashSet<>();
//            protectedBlocks.fetchProtectedBlocks(blocks, world, (int)player.posX, (int)player.posY, (int)player.posZ, protectionWand.blockShowRadius, id);
//            Set<BlockPos> childBlocks = new HashSet<>();
//            if (id == -1) {
//                // Master wand:
//                protectedBlocks.fetchProtectedBlocks(childBlocks, world, (int)player.posX, (int)player.posY, (int)player.posZ, protectionWand.blockShowRadius, -2);
//            }
//            PacketReturnProtectedBlocks msg = new PacketReturnProtectedBlocks(blocks, childBlocks);
//            NEWPacketHandler.INSTANCE.sendTo(msg, player);
//        }
//    }
}
