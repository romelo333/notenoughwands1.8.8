package romelo333.notenoughwands.modules.wands.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record AccelerationWandData(Mode mode) {

    public enum Mode {
        MODE_20("fast", 20, 1.0f),
        MODE_50("faster", 50, 2.0f),
        MODE_100("fastest", 100, 5.0f);

        private final String description;
        private final int amount;
        private final float cost;

        Mode(String description, int amount, float cost) {
            this.description = description;
            this.amount = amount;
            this.cost = cost;
        }

        public String getDescription() {
            return description;
        }

        public int getAmount() {
            return amount;
        }

        public float getCost() {
            return cost;
        }

        public Mode next() {
            return switch (this) {
                case MODE_20 -> MODE_50;
                case MODE_50 -> MODE_100;
                case MODE_100 -> MODE_20;
            };
        }
    }

    public static final AccelerationWandData DEFAULT = new AccelerationWandData(Mode.MODE_20);

    public AccelerationWandData withMode(Mode mode) {
        return new AccelerationWandData(mode);
    }


    public static final Codec<AccelerationWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(Mode::valueOf, Enum::name).fieldOf("mode").forGetter(AccelerationWandData::mode)
    ).apply(instance, AccelerationWandData::new));

    public static final StreamCodec<FriendlyByteBuf, AccelerationWandData> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Mode.class), AccelerationWandData::mode,
            AccelerationWandData::new);
}
