package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.ProtectedBlocks;

public class PacketGetProtectedBlockCount implements IPacket {

    public static final Identifier GET_PROTECTED_BLOCK_COUNT = new Identifier(NotEnoughWands.MODID, "get_protected_block_count");

    private int id;

    @Override
    public Identifier getId() {
        return GET_PROTECTED_BLOCK_COUNT;
    }

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

    public static class Handler extends MessageHandler<PacketGetProtectedBlockCount> {

        @Override
        protected PacketGetProtectedBlockCount createPacket() {
            return new PacketGetProtectedBlockCount();
        }

        @Override
        public void handle(PacketContext context, PacketGetProtectedBlockCount message) {
            PlayerEntity player = context.getPlayer();
            World world = player.getEntityWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(message.id));
            NetworkInit.sendToClient(msg, (ServerPlayerEntity) player);
        }
    }
}