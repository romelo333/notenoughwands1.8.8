package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.network.NEWPacketHandler;

import java.util.function.Supplier;

public class PacketGetProtectedBlockCount {
    private int id;

    public void fromBytes(PacketBuffer buf) {
        id = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(id);
    }

    public PacketGetProtectedBlockCount() {
    }

    public PacketGetProtectedBlockCount(PacketBuffer buf) {
        fromBytes(buf);
    }

    public PacketGetProtectedBlockCount(int id) {
        this.id = id;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity player = ctx.getSender();
            World world = player.getCommandSenderWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(id));
            NEWPacketHandler.INSTANCE.sendTo(msg, ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        });
        ctx.setPacketHandled(true);
    }
}