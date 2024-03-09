package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;
import romelo333.notenoughwands.modules.protectionwand.items.ProtectionWand;
import romelo333.notenoughwands.network.NEWPacketHandler;

import java.util.HashSet;
import java.util.Set;

public record PacketGetProtectedBlocks() implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NotEnoughWands.MODID, "getprotectedblocks");

    public static PacketGetProtectedBlocks create(FriendlyByteBuf buf) {
        return new PacketGetProtectedBlocks();
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
            ctx.player().ifPresent(player -> {
                Level world = player.getCommandSenderWorld();

                ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ProtectionWand protectionWand)) {
                    // Cannot happen normally
                    return;
                }
                int id = protectionWand.getId(heldItem);

                ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
                Set<BlockPos> blocks = new HashSet<>();
                protectedBlocks.fetchProtectedBlocks(blocks, world, (int)player.getX(), (int)player.getY(), (int)player.getZ(), ProtectionWandConfiguration.blockShowRadius.get(), id);
                Set<BlockPos> childBlocks = new HashSet<>();
                if (id == -1) {
                    // Master wand:
                    protectedBlocks.fetchProtectedBlocks(childBlocks, world, (int)player.getX(), (int)player.getY(), (int)player.getZ(), ProtectionWandConfiguration.blockShowRadius.get(), -2);
                }
                PacketReturnProtectedBlocks msg = new PacketReturnProtectedBlocks(blocks, childBlocks);
                NEWPacketHandler.sendToPlayer(msg, player);
            });
        });
    }
}
