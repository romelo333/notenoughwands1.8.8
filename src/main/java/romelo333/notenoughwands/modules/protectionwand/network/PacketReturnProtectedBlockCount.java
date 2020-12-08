package romelo333.notenoughwands.modules.protectionwand.network;


import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketReturnProtectedBlockCount {
    private int count;

    public void fromBytes(PacketBuffer buf) {
        count = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(count);
    }

    public int getCount() {
        return count;
    }

    public PacketReturnProtectedBlockCount() {
    }

    public PacketReturnProtectedBlockCount(PacketBuffer buf) {
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