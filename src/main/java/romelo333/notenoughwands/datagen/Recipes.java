package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import romelo333.notenoughwands.modules.wands.WandsModule;

import java.util.function.Consumer;

public class Recipes extends BaseRecipeProvider {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WandsModule.WAND_CORE.get())
                        .key('X', Items.BLAZE_ROD)
                        .key('n', Items.GOLD_NUGGET)
                        .addCriterion("marble", hasItem(Items.BLAZE_ROD)),
                "Xn ", "nXn", " nX"
        );
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WandsModule.ADVANCED_WAND_CORE.get())
                        .key('t', Items.GHAST_TEAR)
                        .key('x', Items.NETHER_STAR)
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("marble", hasItem(Items.BLAZE_ROD)),
                " x ", "twt", " d "
        );
    }
}
