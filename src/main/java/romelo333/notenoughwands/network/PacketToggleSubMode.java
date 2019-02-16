package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.items.GenericWand;

// @todo fabric
public class PacketToggleSubMode implements IPacket {

    public static final Identifier TOGGLE_SUB_MODE = new Identifier(NotEnoughWands.MODID, "toggle_sub_mode");

    @Override
    public Identifier getId() {
        return TOGGLE_SUB_MODE;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public PacketToggleSubMode() {
    }

    public static class Handler extends MessageHandler<PacketToggleSubMode> {

        @Override
        protected PacketToggleSubMode createPacket() {
            return new PacketToggleSubMode();
        }

        @Override
        public void handle(PacketContext context, PacketToggleSubMode message) {
            PlayerEntity player = context.getPlayer();
            ItemStack heldItem = player.getMainHandStack();
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand) {
                GenericWand genericWand = (GenericWand) (heldItem.getItem());
                genericWand.toggleSubMode(player, heldItem);
            }
        }
    }
}
