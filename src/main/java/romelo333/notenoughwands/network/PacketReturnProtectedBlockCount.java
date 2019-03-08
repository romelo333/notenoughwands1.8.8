package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.Supplier;

public class PacketReturnProtectedBlockCount implements IMessage {
    private int count;

    @Override
    public void fromBytes(ByteBuf buf) {
        count = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(count);
    }

    public int getCount() {
        return count;
    }

    public PacketReturnProtectedBlockCount() {
    }

    public PacketReturnProtectedBlockCount(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketReturnProtectedBlockCount(int count) {
        this.count = count;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnProtectedBlockCountHelper.setProtectedBlocks(this);
        });
        ctx.setPacketHandled(true);
    }
}