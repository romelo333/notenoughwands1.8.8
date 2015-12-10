package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetProtectedBlocks implements IMessage,IMessageHandler<PacketGetProtectedBlocks, PacketReturnProtectedBlocks> {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketGetProtectedBlocks() {
    }

    @Override
    public PacketReturnProtectedBlocks onMessage(PacketGetProtectedBlocks message, MessageContext ctx) {
//        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
//        World world = player.worldObj;
//
//        ItemStack heldItem = player.getHeldItem();
//        if (heldItem == null || !(heldItem.getItem() instanceof ProtectionWand)) {
//            // Cannot happen normally
//            return null;
//        }
//        ProtectionWand protectionWand = (ProtectionWand) heldItem.getItem();
//        int id = protectionWand.getId(heldItem);
//
//        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
//        Set<Coordinate> blocks = new HashSet<Coordinate>();
//        protectedBlocks.fetchProtectedBlocks(blocks, world, (int)player.posX, (int)player.posY, (int)player.posZ, protectionWand.blockShowRadius, id);
//        Set<Coordinate> childBlocks = new HashSet<Coordinate>();
//        if (id == -1) {
//            // Master wand:
//            protectedBlocks.fetchProtectedBlocks(childBlocks, world, (int)player.posX, (int)player.posY, (int)player.posZ, protectionWand.blockShowRadius, -2);
//        }
//        return new PacketReturnProtectedBlocks(blocks, childBlocks);
        return null;
    }

}