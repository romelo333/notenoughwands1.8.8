package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.function.BiConsumer;

public class PacketGetProtectedBlockCount /*implements IMessage*/ {
    private int id;

    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public PacketGetProtectedBlockCount() {
    }

    public PacketGetProtectedBlockCount(int id) {
        this.id = id;
    }

    public static class Handler implements BiConsumer<PacketContext, PacketByteBuf> {

        @Override
        public void accept(PacketContext context, PacketByteBuf packetByteBuf) {
            PacketGetProtectedBlockCount packet = new PacketGetProtectedBlockCount();
            packet.fromBytes(packetByteBuf);
            context.getTaskQueue().execute(() -> handle(context, packet));
        }

        private void handle(PacketContext context, PacketGetProtectedBlockCount message) {
            PlayerEntity player = context.getPlayer();
            World world = player.getEntityWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(message.id));
            NetworkInit.returnProtectedBlockCount(msg, (ServerPlayerEntity) player);
        }
    }
}