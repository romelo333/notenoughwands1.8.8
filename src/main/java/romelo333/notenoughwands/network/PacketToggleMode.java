package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketToggleMode implements IMessage, IMessageHandler<PacketToggleMode, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleMode() {
    }

    @Override
    public IMessage onMessage(PacketToggleMode message, MessageContext ctx) {
        EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
        ItemStack heldItem = playerEntity.getHeldItem();
        if (heldItem != null && heldItem.getItem() instanceof GenericWand) {
            GenericWand genericWand = (GenericWand) (heldItem.getItem());
            genericWand.toggleMode(playerEntity, heldItem);
        }
        return null;
    }

}
