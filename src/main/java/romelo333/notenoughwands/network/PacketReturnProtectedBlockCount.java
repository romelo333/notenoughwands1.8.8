package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

    public PacketReturnProtectedBlockCount(int count) {
        this.count = count;
    }

    public static class Handler implements IMessageHandler<PacketReturnProtectedBlockCount, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnProtectedBlockCount message, MessageContext ctx) {
            MinecraftClient.getInstance().addScheduledTask(() -> ReturnProtectedBlockCountHelper.setProtectedBlocks(message));
            return null;
        }

    }
}