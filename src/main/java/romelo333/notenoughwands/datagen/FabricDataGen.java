package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.DataGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import romelo333.notenoughwands.NotEnoughWands;

public class FabricDataGen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.createPack().addProvider((output, lookup) -> {
            DataGen data = new DataGen(output, lookup);
            NotEnoughWands.MODULES.datagen(data, lookup);
            data.generate();
            return data;
        });
    }
}
