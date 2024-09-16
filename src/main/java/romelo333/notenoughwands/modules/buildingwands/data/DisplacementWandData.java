package romelo333.notenoughwands.modules.buildingwands.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.Set;

public record DisplacementWandData(Mode mode) {

    public static final int MAX_UNDO = 3;

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

    public static final DisplacementWandData DEFAULT = new DisplacementWandData(Mode.MODE_3X3);

    public static final Codec<DisplacementWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Mode::valueOf, Enum::name).fieldOf("mode").forGetter(DisplacementWandData::mode)
    ).apply(instance, DisplacementWandData::new));

    public static final StreamCodec<FriendlyByteBuf, DisplacementWandData> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Mode.class), DisplacementWandData::mode,
            DisplacementWandData::new);
}
