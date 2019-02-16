package romelo333.notenoughwands.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import romelo333.notenoughwands.NotEnoughWands;

public class PacketReturnProtectedBlockCount implements IPacket {

    public static final Identifier RETURN_PROTECTED_BLOCK_COUNT = new Identifier(NotEnoughWands.MODID, "return_protected_block_count");

    private int count;

    @Override
    public Identifier getId() {
        return RETURN_PROTECTED_BLOCK_COUNT;
    }

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

    public static class Handler extends MessageHandler<PacketReturnProtectedBlockCount> {

        @Override
        protected PacketReturnProtectedBlockCount createPacket() {
            return new PacketReturnProtectedBlockCount();
        }

        @Override
        public void handle(PacketContext context, PacketReturnProtectedBlockCount message) {
            ReturnProtectedBlockCountHelper.setProtectedBlocks(message);
        }

    }
}