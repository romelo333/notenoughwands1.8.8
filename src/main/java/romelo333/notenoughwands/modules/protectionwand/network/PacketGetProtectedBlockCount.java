package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.network.NEWPacketHandler;

import java.util.function.Supplier;

public class PacketGetProtectedBlockCount {
    private int id;

    public void fromBytes(FriendlyByteBuf buf) {
        id = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(id);
    }

    public PacketGetProtectedBlockCount(FriendlyByteBuf buf) {
        fromBytes(buf);
    }

    public PacketGetProtectedBlockCount(int id) {
        this.id = id;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            Level world = player.getCommandSenderWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(id));
            NEWPacketHandler.INSTANCE.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}