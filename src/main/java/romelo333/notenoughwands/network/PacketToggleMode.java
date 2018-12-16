package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.PacketByteBuf;
import romelo333.notenoughwands.items.GenericWand;

import java.util.function.BiConsumer;

public class PacketToggleMode /*implements IMessage*/ {

//    @Override
    public void fromBytes(ByteBuf buf) {
    }

//    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleMode() {
    }

    public static class Handler implements BiConsumer<PacketContext, PacketByteBuf> {
        @Override
        public void accept(PacketContext context, PacketByteBuf packetByteBuf) {
            PacketToggleMode packet = new PacketToggleMode();
            packet.fromBytes(packetByteBuf);
            context.getTaskQueue().execute(() -> handle(context, packet));
        }

        private void handle(PacketContext context, PacketToggleMode message) {
            PlayerEntity player = context.getPlayer();
            ItemStack heldItem = player.getMainHandStack();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
                GenericWand genericWand = (GenericWand) (heldItem.getItem());
                genericWand.toggleMode(player, heldItem);
            }
        }
    }
}
