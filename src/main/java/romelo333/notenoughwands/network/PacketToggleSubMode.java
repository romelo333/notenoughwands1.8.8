package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.PacketByteBuf;
import romelo333.notenoughwands.items.GenericWand;

import java.util.function.BiConsumer;

// @todo fabric
public class PacketToggleSubMode /*implements IMessage*/ {

    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleSubMode() {
    }

    public static class Handler implements BiConsumer<PacketContext, PacketByteBuf> {
        @Override
        public void accept(PacketContext context, PacketByteBuf packetByteBuf) {
            PacketToggleSubMode packet = new PacketToggleSubMode();
            packet.fromBytes(packetByteBuf);
            context.getTaskQueue().execute(() -> handle(context, packet));
        }

        private void handle(PacketContext context, PacketToggleSubMode message) {
            PlayerEntity player = context.getPlayer();
            ItemStack heldItem = player.getMainHandStack();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
                GenericWand genericWand = (GenericWand) (heldItem.getItem());
                genericWand.toggleSubMode(player, heldItem);
            }
        }
    }
}
