package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.lightwand.LightModule;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, NotEnoughWands.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        singleTextureBlock(LightModule.LIGHT.get(), BLOCK_FOLDER + "/light", "block/empty");
    }
}
