package romelo333.notenoughwands.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;

import java.util.function.Supplier;

public class PacketToggleMode {

    public void fromBytes(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public PacketToggleMode() {
    }

    public PacketToggleMode(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Player playerEntity = ctx.getSender();
            ItemStack heldItem = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
                GenericWand genericWand = (GenericWand) (heldItem.getItem());
                genericWand.toggleMode(playerEntity, heldItem);
            }
        });
        ctx.setPacketHandled(true);
    }
}
