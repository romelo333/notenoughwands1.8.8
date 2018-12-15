package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.util.PacketByteBuf;

import java.util.function.BiConsumer;

public class PacketReturnProtectedBlockCount /*implements IMessage*/ {
    private int count;

//    @Override
    public void fromBytes(ByteBuf buf) {
        count = buf.readInt();
    }

//    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(count);
    }

    public int getCount() {
        return count;
    }

    public PacketReturnProtectedBlockCount() {
    }

    public PacketReturnProtectedBlockCount(int count) {
        this.count = count;
    }

    public static class Handler implements BiConsumer<PacketContext, PacketByteBuf> {

        @Override
        public void accept(PacketContext context, PacketByteBuf packetByteBuf) {
            PacketReturnProtectedBlockCount packet = new PacketReturnProtectedBlockCount();
            packet.fromBytes(packetByteBuf);
            context.getTaskQueue().execute(() -> handle(context, packet));
        }

        private void handle(PacketContext context, PacketReturnProtectedBlockCount message) {
            ReturnProtectedBlockCountHelper.setProtectedBlocks(message);
        }

    }
}