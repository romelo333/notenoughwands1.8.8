package romelo333.notenoughwands.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;

import java.util.function.Supplier;

public class PacketToggleSubMode {

    public void fromBytes(PacketBuffer buf) {
    }

    public void toBytes(PacketBuffer buf) {
    }

    public PacketToggleSubMode() {
    }

    public PacketToggleSubMode(PacketBuffer buf) {
        fromBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity playerEntity = ctx.getSender();
            ItemStack heldItem = playerEntity.getHeldItem(Hand.MAIN_HAND);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
                GenericWand genericWand = (GenericWand) (heldItem.getItem());
                genericWand.toggleSubMode(playerEntity, heldItem);
            }
        });
        ctx.setPacketHandled(true);
    }
}
