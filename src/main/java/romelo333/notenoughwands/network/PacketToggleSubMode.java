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

public record PacketToggleSubMode() implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(NotEnoughWands.MODID, "togglesubmode");
    public static final CustomPacketPayload.Type<PacketToggleSubMode> TYPE = new Type<>(ID);

    public static final PacketToggleSubMode INSTANCE = new PacketToggleSubMode();

    public static final StreamCodec<FriendlyByteBuf, PacketToggleSubMode> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand genericWand) {
                genericWand.toggleSubMode(player, heldItem);
            }
        });
    }
}
