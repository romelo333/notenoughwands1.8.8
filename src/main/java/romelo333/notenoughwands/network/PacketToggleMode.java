package romelo333.notenoughwands.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;

public record PacketToggleMode() implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(NotEnoughWands.MODID, "togglemode");
    public static final CustomPacketPayload.Type<PacketToggleMode> TYPE = new Type<>(ID);

    public static final PacketToggleMode INSTANCE = new PacketToggleMode();

    public static final StreamCodec<FriendlyByteBuf, PacketToggleMode> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand genericWand) {
                genericWand.toggleMode(player, heldItem);
            }
        });
    }
}
