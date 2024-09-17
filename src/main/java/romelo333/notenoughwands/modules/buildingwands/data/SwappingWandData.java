package romelo333.notenoughwands.modules.buildingwands.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record SwappingWandData(Mode mode, BlockState state, Boolean offhand, Float hardness) {

    public enum Mode {
        MODE_3X3("3x3"),
        MODE_5X5("5x5"),
        MODE_7X7("7x7"),
        MODE_SINGLE("single");

        private final String description;

        Mode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public Mode next() {
            return switch (this) {
                case MODE_3X3 -> MODE_5X5;
                case MODE_5X5 -> MODE_7X7;
                case MODE_7X7 -> MODE_SINGLE;
                case MODE_SINGLE -> MODE_3X3;
            };
        }
    }

    public static final SwappingWandData DEFAULT = new SwappingWandData(Mode.MODE_3X3, Blocks.AIR.defaultBlockState(), false, 0.0f);

    public SwappingWandData withMode(Mode mode) {
        return new SwappingWandData(mode, state, offhand, hardness);
    }

    public SwappingWandData withOffhand(boolean b) {
        return new SwappingWandData(mode, state, b, hardness);
    }

    public SwappingWandData withHardness(float hardness) {
        return new SwappingWandData(mode, state, offhand, hardness);
    }

    public static final Codec<SwappingWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Mode::valueOf, Enum::name).fieldOf("mode").forGetter(SwappingWandData::mode),
            BlockState.CODEC.fieldOf("state").forGetter(SwappingWandData::state),
            Codec.BOOL.fieldOf("offhand").forGetter(SwappingWandData::offhand),
            Codec.FLOAT.optionalFieldOf("hardness", 0.0f).forGetter(SwappingWandData::hardness)
    ).apply(instance, SwappingWandData::new));

    public static final StreamCodec<FriendlyByteBuf, SwappingWandData> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Mode.class), SwappingWandData::mode,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), SwappingWandData::state,
            ByteBufCodecs.BOOL, SwappingWandData::offhand,
            ByteBufCodecs.FLOAT, SwappingWandData::hardness,
            SwappingWandData::new);
}
