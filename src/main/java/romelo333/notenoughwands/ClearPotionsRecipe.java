package romelo333.notenoughwands;

import com.google.common.collect.Lists;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class ClearPotionsRecipe extends ShapelessRecipes {
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack potion = ItemStackTools.getEmptyStack();
        ItemStack wand = ItemStackTools.getEmptyStack();
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemStackTools.isValid(stack) && stack.getItem() == ModItems.potionWand) {
                wand = stack;
            } else if (ItemStackTools.isValid(stack) && stack.getItem() == Items.GLASS_BOTTLE) {
                potion = stack;
            }
        }

        ItemStack result = super.getCraftingResult(inv);
        NBTTagCompound tagCompound = wand.getTagCompound();
        if (tagCompound==null){
            tagCompound=new NBTTagCompound();
        }
        tagCompound= tagCompound.copy();
        NBTTagList list = new NBTTagList();
        tagCompound.setTag("effects",list);
        result.setTagCompound(tagCompound);
        return result;
    }

    public ClearPotionsRecipe(){
        super(new ItemStack(ModItems.potionWand), Lists.asList(new ItemStack(ModItems.potionWand), new ItemStack(Items.GLASS_BOTTLE), new ItemStack[0]));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int foundWand = 0;
        int foundPotion = 0;
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemStackTools.isValid(stack) && stack.getItem() == ModItems.potionWand) {
                foundWand++;
            } else if (ItemStackTools.isValid(stack) && stack.getItem() == Items.GLASS_BOTTLE) {
                foundPotion++;
            } else if (ItemStackTools.isValid(stack)) {
                return false;
            }
        }
        return foundWand == 1 && foundPotion == 1;
    }
}
