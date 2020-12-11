package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import romelo333.notenoughwands.NotEnoughWands;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, NotEnoughWands.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
    }

    @Override
    public String getName() {
        return "Not Enough Wands Item Models";
    }
}
