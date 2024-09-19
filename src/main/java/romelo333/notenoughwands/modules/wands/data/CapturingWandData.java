package romelo333.notenoughwands.modules.wands.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record CapturingWandData(ResourceLocation type, CompoundTag tag) {

    public static final CapturingWandData DEFAULT = new CapturingWandData(null, new CompoundTag());

    public static final Codec<CapturingWandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("type").forGetter(d -> Optional.ofNullable(d.type())),
            CompoundTag.CODEC.fieldOf("tag").forGetter(CapturingWandData::tag)
    ).apply(instance, (type, tag) -> new CapturingWandData(type.orElse(null), tag)));

    public static final StreamCodec<FriendlyByteBuf, CapturingWandData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), d -> Optional.ofNullable(d.type()),
            ByteBufCodecs.compoundTagCodec(NbtAccounter::unlimitedHeap), CapturingWandData::tag,
            (type, tag) -> new CapturingWandData(type.orElse(null), tag));
}
