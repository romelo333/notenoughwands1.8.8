package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.BaseRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandModule;
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
                        .addCriterion("rod", hasItem(Items.BLAZE_ROD)),
                "Xn ", "nXn", " nX"
        );
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WandsModule.ADVANCED_WAND_CORE.get())
                        .key('t', Items.GHAST_TEAR)
                        .key('x', Items.NETHER_STAR)
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("rod", hasItem(Items.BLAZE_ROD)),
                " x ", "twt", " d "
        );

        build(consumer, ShapedRecipeBuilder.shapedRecipe(BuildingWandsModule.BUILDING_WAND.get())
                        .key('x', Items.BRICK)
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "xx ", "xw ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shapedRecipe(BuildingWandsModule.DISPLACEMENT_WAND.get())
                        .key('x', Items.BRICK)
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "ox ", "xw ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shapedRecipe(BuildingWandsModule.MOVING_WAND.get())
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "ro ", "ow ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shapedRecipe(BuildingWandsModule.SWAPPING_WAND.get())
                        .key('x', Items.GLOWSTONE)
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "Rx ", "xw ", "  w"
        );

        build(consumer, ShapedRecipeBuilder.shapedRecipe(LightModule.ILLUMINATION_WAND.get())
                        .key('x', Items.GLOWSTONE_DUST)
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "xx ", "xw ", "  w"
        );

        build(consumer, ShapedRecipeBuilder.shapedRecipe(ProtectionWandModule.PROTECTION_WAND.get())
                        .key('x', Items.COMPARATOR)
                        .key('w', WandsModule.ADVANCED_WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "xo ", "ow ", "  w"
        );

        build(consumer, ShapedRecipeBuilder.shapedRecipe(WandsModule.CAPTURING_WAND.get())
                        .key('x', Items.ROTTEN_FLESH)
                        .key('w', WandsModule.ADVANCED_WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "dx ", "xw ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WandsModule.TELEPORTATION_WAND.get())
                        .key('w', WandsModule.WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "oo ", "ow ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shapedRecipe(WandsModule.ACCELERATION_WAND.get())
                        .key('x', Items.CLOCK)
                        .key('w', WandsModule.ADVANCED_WAND_CORE.get())
                        .addCriterion("core", hasItem(WandsModule.WAND_CORE.get())),
                "xr ", "rw ", "  w"
        );
    }
}
