package romelo333.notenoughwands.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnProtectedBlockCountHandler implements IMessageHandler<PacketReturnProtectedBlockCount, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnProtectedBlockCount message, MessageContext ctx) {
        ReturnProtectedBlockCountHelper.setProtectedBlocks(message);
        return null;
    }

}