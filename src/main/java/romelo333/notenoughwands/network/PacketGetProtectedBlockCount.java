package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.function.Supplier;

public class PacketGetProtectedBlockCount implements IMessage {
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

    public PacketGetProtectedBlockCount(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetProtectedBlockCount(int id) {
        this.id = id;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP player = ctx.getSender();
            World world = player.getEntityWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(id));
            NEWPacketHandler.INSTANCE.sendTo(msg, player);
        });
        ctx.setPacketHandled(true);
    }
}