package romelo333.notenoughwands;


import net.minecraftforge.oredict.RecipeSorter;
import romelo333.notenoughwands.varia.ContainerToItemRecipe;

public class ModCrafting {
    static {
        RecipeSorter.register("NotEnoughWands:containertoitem", ContainerToItemRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        RecipeSorter.register("NotEnoughWands:addpotion", AddPotionRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("NotEnoughWands:clearpotions", ClearPotionsRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }
}
