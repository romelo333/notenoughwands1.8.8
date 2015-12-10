package romelo333.notenoughwands.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnProtectedBlocksHandler implements IMessageHandler<PacketReturnProtectedBlocks, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnProtectedBlocks message, MessageContext ctx) {
        ReturnProtectedBlocksHelper.setProtectedBlocks(message);
        return null;
    }

}