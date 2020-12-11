package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
    }

    @Override
    public String getName() {
        return "Not Enough Wands LootTables";
    }
}
