package romelo333.notenoughwands.modules.protectionwand.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketReturnProtectedBlockCount {
    private int count;

    public void fromBytes(FriendlyByteBuf buf) {
        count = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(count);
    }

    public int getCount() {
        return count;
    }

    public PacketReturnProtectedBlockCount() {
    }

    public PacketReturnProtectedBlockCount(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnProtectedBlockCount(int count) {
        this.count = count;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnProtectedBlockCountHelper.setProtectedBlocks(this);
        });
        ctx.setPacketHandled(true);
    }
}