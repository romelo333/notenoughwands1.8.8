package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import romelo333.notenoughwands.ProtectedBlocks;

public class PacketGetProtectedBlockCount implements IMessage,IMessageHandler<PacketGetProtectedBlockCount, PacketReturnProtectedBlockCount> {
    private int id;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public PacketGetProtectedBlockCount() {
    }

    public PacketGetProtectedBlockCount(int id) {
        this.id = id;
    }

    @Override
    public PacketReturnProtectedBlockCount onMessage(PacketGetProtectedBlockCount message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;

        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        return new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(message.id));
    }

}