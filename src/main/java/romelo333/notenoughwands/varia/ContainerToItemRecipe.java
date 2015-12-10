package romelo333.notenoughwands.varia;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerToItemRecipe extends ShapedRecipes {
    private Item itemToInheritFrom;

    public ContainerToItemRecipe(ItemStack[] grid, int index, ItemStack output) {
        super(3, 3, grid, output);
        itemToInheritFrom = grid[index].getItem();
    }

    private NBTTagCompound getNBTFromObject(InventoryCrafting inventoryCrafting) {
        for (int i = 0 ; i < inventoryCrafting.getSizeInventory() ; i++) {
            ItemStack stack = inventoryCrafting.getStackInSlot(i);
            if (stack != null && stack.getItem() != null) {
                Item o = stack.getItem();
                if (itemToInheritFrom.equals(o)) {
                    return stack.getTagCompound();
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        ItemStack stack = super.getCraftingResult(inventoryCrafting);
        if (stack != null) {
            NBTTagCompound tagCompound = getNBTFromObject(inventoryCrafting);
            if (tagCompound != null) {
                int id = tagCompound.getInteger("id");
                NBTTagCompound newtag = new NBTTagCompound();
                newtag.setInteger("id", id);
                stack.setTagCompound(newtag);
            }
        }
        return stack;
    }

}
