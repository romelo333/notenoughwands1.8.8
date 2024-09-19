package romelo333.notenoughwands.modules.protectionwand.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record ProtectionWandData(Mode mode, Integer id) {

    public enum Mode {
        MODE_PROTECT("protect"),
        MODE_UNPROTECT("unprotect"),
        MODE_CLEAR("clear all");

        private final String description;

        Mode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public ProtectionWandData.Mode next() {
            return switch (this) {
                case MODE_PROTECT -> MODE_UNPROTECT;
                case MODE_UNPROTECT -> MODE_CLEAR;
                case MODE_CLEAR -> MODE_PROTECT;
            };
        }
    }

    public ProtectionWandData withMode(Mode mode) {
        return new ProtectionWandData(mode, id);
    }

    public ProtectionWandData withId(int id) {
        return new ProtectionWandData(mode, id);
    }

    public static final ProtectionWandData DEFAULT = new ProtectionWandData(Mode.MODE_PROTECT, -1);

    public static final Codec<ProtectionWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.xmap(ProtectionWandData.Mode::valueOf, Enum::name).fieldOf("mode").forGetter(ProtectionWandData::mode),
            Codec.INT.fieldOf("id").forGetter(ProtectionWandData::id)
    ).apply(instance, ProtectionWandData::new));

    public static final StreamCodec<FriendlyByteBuf, ProtectionWandData> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(ProtectionWandData.Mode.class), ProtectionWandData::mode,
            ByteBufCodecs.INT, ProtectionWandData::id,
            ProtectionWandData::new);
}
