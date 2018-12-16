package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.items.GenericWand;

public class PacketToggleMode implements IPacket {

    public static final Identifier TOGGLE_MODE = new Identifier(NotEnoughWands.MODID, "toggle_mode");

    @Override
    public Identifier getId() {
        return TOGGLE_MODE;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleMode() {
    }

    public static class Handler extends MessageHandler<PacketToggleMode> {

        @Override
        protected PacketToggleMode createPacket() {
            return new PacketToggleMode();
        }

        @Override
        public void handle(PacketContext context, PacketToggleMode message) {
            PlayerEntity player = context.getPlayer();
            ItemStack heldItem = player.getMainHandStack();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
                GenericWand genericWand = (GenericWand) (heldItem.getItem());
                genericWand.toggleMode(player, heldItem);
            }
        }
    }
}
