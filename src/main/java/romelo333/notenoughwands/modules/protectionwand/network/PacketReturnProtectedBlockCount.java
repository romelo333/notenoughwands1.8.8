package romelo333.notenoughwands.modules.protectionwand.network;


import mcjty.lib.network.CustomPacketPayload;
import mcjty.lib.network.PlayPayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import romelo333.notenoughwands.NotEnoughWands;

public record PacketReturnProtectedBlockCount(int count) implements CustomPacketPayload {

    public static ResourceLocation ID = new ResourceLocation(NotEnoughWands.MODID, "returnprotectedblockcount");

    public static PacketReturnProtectedBlockCount create(FriendlyByteBuf buf) {
        return new PacketReturnProtectedBlockCount(buf.readInt());
    }

    public static PacketReturnProtectedBlockCount create(int count) {
        return new PacketReturnProtectedBlockCount(count);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(count);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public int getCount() {
        return count;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ReturnProtectedBlockCountHelper.setProtectedBlocks(this);
        });
    }
}