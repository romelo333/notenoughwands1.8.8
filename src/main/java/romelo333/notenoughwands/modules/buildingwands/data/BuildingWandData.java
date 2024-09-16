package romelo333.notenoughwands.modules.buildingwands.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record BuildingWandData(Mode mode, OrientationMode orientationMode, List<UndoState> undoStates) {

    public static final int MAX_UNDO = 3;

    public enum Mode {
        MODE_9("9 blocks", 9),
        MODE_9ROW("9 blocks row", 9),
        MODE_25("25 blocks", 25),
        MODE_25ROW("25 blocks row", 25),
        MODE_SINGLE("single", 1);

        private final String description;
        private final int amount;

        Mode(String description, int amount) {
            this.description = description;
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public int getAmount() {
            return amount;
        }

        public Mode next() {
            return switch (this) {
                case MODE_9 -> MODE_9ROW;
                case MODE_9ROW -> MODE_25;
                case MODE_25 -> MODE_25ROW;
                case MODE_25ROW -> MODE_SINGLE;
                case MODE_SINGLE -> MODE_9;
            };
        }
    }

    public enum OrientationMode {
        NORMAL("Normal"),
        ROTATED("Rotated");

        private final String description;

        OrientationMode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public OrientationMode next() {
            return switch (this) {
                case NORMAL -> ROTATED;
                case ROTATED -> NORMAL;
            };
        }
    }

    public record UndoState(ResourceKey<Level> dimension, BlockState state, Set<BlockPos> positions) {
    }

    // Find the index of the undostate that contains the given position (for the dimension)
    public int findUndoStateIndex(ResourceKey<Level> dimension, BlockPos pos) {
        for (int i = 0 ; i < undoStates.size() ; i++) {
            UndoState undoState = undoStates.get(i);
            if (undoState.dimension().equals(dimension) && undoState.positions().contains(pos)) {
                return i;
            }
        }
        return -1;
    }


    public static final BuildingWandData DEFAULT = new BuildingWandData(Mode.MODE_9, OrientationMode.NORMAL, new ArrayList<>());

    private static final Codec<UndoState> UNDO_STATE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(UndoState::dimension),
            BlockState.CODEC.fieldOf("state").forGetter(UndoState::state),
            Codec.list(BlockPos.CODEC).fieldOf("positions").forGetter(undoState -> new ArrayList<>(undoState.positions()))
    ).apply(instance, (level, state, positions) -> new UndoState(level, state, Set.copyOf(positions))));

    private static final StreamCodec<FriendlyByteBuf, UndoState> UNDO_STATE_STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), UndoState::dimension,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), UndoState::state,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), UndoState::positions,
            UndoState::new);

    public static final Codec<BuildingWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Mode::valueOf, Enum::name).fieldOf("mode").forGetter(BuildingWandData::mode),
            Codec.STRING.xmap(OrientationMode::valueOf, Enum::name).fieldOf("orientationMode").forGetter(BuildingWandData::orientationMode),
            Codec.list(UNDO_STATE_CODEC).fieldOf("undo").forGetter(BuildingWandData::undoStates)
    ).apply(instance, BuildingWandData::new));

    public static final StreamCodec<FriendlyByteBuf, BuildingWandData> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Mode.class), BuildingWandData::mode,
            NeoForgeStreamCodecs.enumCodec(OrientationMode.class), BuildingWandData::orientationMode,
            UNDO_STATE_STREAM_CODEC.apply(ByteBufCodecs.list()), BuildingWandData::undoStates,
            BuildingWandData::new);
}
