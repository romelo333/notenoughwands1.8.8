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
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        build(consumer, ShapedRecipeBuilder.shaped(WandsModule.WAND_CORE.get())
                        .define('X', Items.BLAZE_ROD)
                        .define('n', Items.GOLD_NUGGET)
                        .unlockedBy("rod", has(Items.BLAZE_ROD)),
                "Xn ", "nXn", " nX"
        );
        build(consumer, ShapedRecipeBuilder.shaped(WandsModule.ADVANCED_WAND_CORE.get())
                        .define('t', Items.GHAST_TEAR)
                        .define('x', Items.NETHER_STAR)
                        .define('w', WandsModule.WAND_CORE.get())
                        .unlockedBy("rod", has(Items.BLAZE_ROD)),
                " x ", "twt", " d "
        );

        build(consumer, ShapedRecipeBuilder.shaped(BuildingWandsModule.BUILDING_WAND.get())
                        .define('x', Items.BRICK)
                        .define('w', WandsModule.WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "xx ", "xw ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shaped(BuildingWandsModule.DISPLACEMENT_WAND.get())
                        .define('x', Items.BRICK)
                        .define('w', WandsModule.WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "ox ", "xw ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shaped(BuildingWandsModule.MOVING_WAND.get())
                        .define('w', WandsModule.WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "ro ", "ow ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shaped(BuildingWandsModule.SWAPPING_WAND.get())
                        .define('x', Items.GLOWSTONE)
                        .define('w', WandsModule.WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "Rx ", "xw ", "  w"
        );

        build(consumer, ShapedRecipeBuilder.shaped(LightModule.ILLUMINATION_WAND.get())
                        .define('x', Items.GLOWSTONE_DUST)
                        .define('w', WandsModule.WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "xx ", "xw ", "  w"
        );

        build(consumer, ShapedRecipeBuilder.shaped(ProtectionWandModule.PROTECTION_WAND.get())
                        .define('x', Items.COMPARATOR)
                        .define('w', WandsModule.ADVANCED_WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "xo ", "ow ", "  w"
        );

        build(consumer, ShapedRecipeBuilder.shaped(WandsModule.CAPTURING_WAND.get())
                        .define('x', Items.ROTTEN_FLESH)
                        .define('w', WandsModule.ADVANCED_WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "dx ", "xw ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shaped(WandsModule.TELEPORTATION_WAND.get())
                        .define('w', WandsModule.WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "oo ", "ow ", "  w"
        );
        build(consumer, ShapedRecipeBuilder.shaped(WandsModule.ACCELERATION_WAND.get())
                        .define('x', Items.CLOCK)
                        .define('w', WandsModule.ADVANCED_WAND_CORE.get())
                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                "xr ", "rw ", "  w"
        );
    }
}
