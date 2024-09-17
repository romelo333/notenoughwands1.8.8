package romelo333.notenoughwands.modules.buildingwands.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record MovingWandData(BlockState state, CompoundTag tag) {

    public static final MovingWandData DEFAULT = new MovingWandData(Blocks.AIR.defaultBlockState(), new CompoundTag());

    public static final Codec<MovingWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(MovingWandData::state),
            CompoundTag.CODEC.fieldOf("tag").forGetter(MovingWandData::tag)
    ).apply(instance, MovingWandData::new));

    public static final StreamCodec<FriendlyByteBuf, MovingWandData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), MovingWandData::state,
            ByteBufCodecs.compoundTagCodec(NbtAccounter::unlimitedHeap), MovingWandData::tag,
            MovingWandData::new);
}
