package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.network.NEWPacketHandler;

public record PacketGetProtectedBlockCount(Integer protectionId) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NotEnoughWands.MODID, "getprotectedblockcount");

    public static PacketGetProtectedBlockCount create(FriendlyByteBuf buf) {
        return new PacketGetProtectedBlockCount(buf.readInt());
    }

    public static PacketGetProtectedBlockCount create(Integer protectionId) {
        return new PacketGetProtectedBlockCount(protectionId);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(protectionId);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                Level world = player.getCommandSenderWorld();

                ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
                PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(protectionId));
                NEWPacketHandler.sendToPlayer(msg, player);
            });
        });
    }
}