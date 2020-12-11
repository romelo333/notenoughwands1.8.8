package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import romelo333.notenoughwands.NotEnoughWands;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, NotEnoughWands.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
    }
}
