package romelo333.notenoughwands;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ClearPotionsRecipe extends ShapedRecipes {
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = super.getCraftingResult(inv);
        NBTTagCompound tagCompound = inv.getStackInSlot(0).getTagCompound();
        if (tagCompound==null){
            tagCompound=new NBTTagCompound();
        }
        tagCompound=(NBTTagCompound)tagCompound.copy();
        NBTTagList list = new NBTTagList();
        tagCompound.setTag("effects",list);
        result.setTagCompound(tagCompound);
        return result;
    }

    public ClearPotionsRecipe(){
        super(2, 1, new ItemStack[]{new ItemStack(ModItems.potionWand),new ItemStack(Items.glass_bottle)}, new ItemStack(ModItems.potionWand));

    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack wand = inv.getStackInSlot(0);
        if (wand==null){
            return false;
        }
        if (wand.getItem()!=ModItems.potionWand){
            return false;
        }
        ItemStack potion = inv.getStackInSlot(1);
        if (potion==null){
            return false;
        }
        if (potion.getItem()!=Items.glass_bottle){
            return false;
        }
        return true;
    }
}
