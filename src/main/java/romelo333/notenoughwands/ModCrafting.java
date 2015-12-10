package romelo333.notenoughwands;


import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import romelo333.notenoughwands.varia.ContainerToItemRecipe;

public class ModCrafting {
    static {
        RecipeSorter.register("NotEnoughWands:containertoitem", ContainerToItemRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
    }


    public static void init() {
        GameRegistry.addRecipe(new ItemStack(ModItems.wandCore), "bn ", "nbn", " nb", 'b', Items.blaze_rod, 'n', Items.gold_nugget);
        GameRegistry.addRecipe(new ItemStack(ModItems.advancedWandCore),
                " x ",
                "twt",
                " d ",
                'w', ModItems.wandCore, 'x', Items.nether_star, 't', Items.ghast_tear, 'd', Items.diamond);

        GenericWand.setupCrafting();
    }
}
