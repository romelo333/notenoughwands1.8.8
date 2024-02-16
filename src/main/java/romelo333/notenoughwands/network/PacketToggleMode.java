package romelo333.notenoughwands.network;

import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;

public record PacketToggleMode() implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NotEnoughWands.MODID, "togglemode");

    public static PacketToggleMode create(FriendlyByteBuf buf) {
        return new PacketToggleMode();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(playerEntity -> {
                ItemStack heldItem = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);
                if (!heldItem.isEmpty() && heldItem.getItem() instanceof GenericWand genericWand) {
                    genericWand.toggleMode(playerEntity, heldItem);
                }
            });
        });
    }
}
