package romelo333.notenoughwands.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import romelo333.notenoughwands.setup.Registration;

import java.util.function.Supplier;

public class EnergyItem {

    public static final Supplier<DataComponentType<Integer>> ENERGY_COMPONENT = Registration.COMPONENTS.register("energy", () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build());


}
