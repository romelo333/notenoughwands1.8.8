package romelo333.notenoughwands;


import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import romelo333.notenoughwands.Items.GenericWand;
import romelo333.notenoughwands.varia.ContainerToItemRecipe;

public class ModCrafting {
    static {
        RecipeSorter.register("NotEnoughWands:containertoitem", ContainerToItemRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("NotEnoughWands:addpotion", AddPotionRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("NotEnoughWands:clearpotions", ClearPotionsRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }


    public static void init() {
        GameRegistry.addRecipe(new ItemStack(ModItems.wandCore), "bn ", "nbn", " nb", 'b', Items.BLAZE_ROD, 'n', Items.GOLD_NUGGET);
        GameRegistry.addRecipe(new ItemStack(ModItems.advancedWandCore),
                " x ",
                "twt",
                " d ",
                'w', ModItems.wandCore, 'x', Items.NETHER_STAR, 't', Items.GHAST_TEAR, 'd', Items.DIAMOND);

        GenericWand.setupCrafting();
    }
}
