package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import romelo333.notenoughwands.Items.GenericWand;

import java.util.function.Supplier;

public class PacketToggleMode implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleMode() {
    }

    public PacketToggleMode(ByteBuf buf) {
        fromBytes(buf);
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP playerEntity = ctx.getSender();
            ItemStack heldItem = playerEntity.getHeldItem(EnumHand.MAIN_HAND);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
                GenericWand genericWand = (GenericWand) (heldItem.getItem());
                genericWand.toggleMode(playerEntity, heldItem);
            }
        });
        ctx.setPacketHandled(true);
    }
}
