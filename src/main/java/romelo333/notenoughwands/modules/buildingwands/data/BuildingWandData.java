package romelo333.notenoughwands.modules.buildingwands.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record BuildingWandData(Mode mode, OrientationMode orientationMode, List<UndoState> undoStates) {

    public enum Mode {
        MODE_9("9 blocks"),
        MODE_9ROW("9 blocks row"),
        MODE_25("25 blocks"),
        MODE_25ROW("25 blocks row"),
        MODE_SINGLE("single");

        private final String description;

        Mode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum OrientationMode {
        HORIZONTAL("horizontal"),
        VERTICAL("vertical");

        private final String description;

        OrientationMode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public record UndoState(ResourceKey<Level> dimension, BlockState state, Set<BlockPos> positions) {

    }

    public static final BuildingWandData DEFAULT = new BuildingWandData(Mode.MODE_9, OrientationMode.HORIZONTAL, new ArrayList<>());

    private static final Codec<UndoState> UNDO_STATE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(UndoState::dimension),
            BlockState.CODEC.fieldOf("state").forGetter(UndoState::state),
            Codec.list(BlockPos.CODEC).fieldOf("positions").forGetter(undoState -> new ArrayList<>(undoState.positions()))
    ).apply(instance, (level, state, positions) -> new UndoState(level, state, Set.copyOf(positions))));

    private static final StreamCodec<FriendlyByteBuf, UndoState> UNDO_STATE_STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), UndoState::dimension,
            BlockState.STREAM_CODEC, UndoState::state,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), UndoState::positions,
            UndoState::new);

    public static final Codec<BuildingWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Mode::valueOf, Enum::name).fieldOf("mode").forGetter(BuildingWandData::mode),
            Codec.STRING.xmap(OrientationMode::valueOf, Enum::name).fieldOf("orientationMode").forGetter(BuildingWandData::orientationMode),
            Codec.list(UNDO_STATE_CODEC).fieldOf("undo").forGetter(BuildingWandData::undoStates)
    ).apply(instance, BuildingWandData::new));

    public static final StreamCodec<FriendlyByteBuf, BuildingWandData> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Mode.class), BuildingWandData::mode,
            NeoForgeStreamCodecs.enumCodec(OrientationMode.class), BuildingWandData::orientationMode,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), BuildingWandData::lines,
            BuildingWandData::new);
}
