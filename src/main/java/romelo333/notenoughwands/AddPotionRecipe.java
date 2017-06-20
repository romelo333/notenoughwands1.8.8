package romelo333.notenoughwands;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class AddPotionRecipe extends ShapelessRecipes {
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack potion = ItemStack.EMPTY;
        ItemStack wand = ItemStack.EMPTY;
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.potionWand) {
                wand = stack;
            } else if (!stack.isEmpty() && stack.getItem() == Items.POTIONITEM) {
                potion = stack;
            }
        }

        ItemStack result = super.getCraftingResult(inv);
        NBTTagCompound tagCompound = wand.getTagCompound();
        if (tagCompound==null){
            tagCompound=new NBTTagCompound();
        }
        tagCompound = tagCompound.copy();
        NBTTagList list = tagCompound.getTagList("effects", Constants.NBT.TAG_COMPOUND);
        List<PotionEffect> effectsFromStack = PotionUtils.getEffectsFromStack(potion);
        for (PotionEffect effect : effectsFromStack) {
            NBTTagCompound effecttag = new NBTTagCompound();
            effect.writeCustomPotionEffectToNBT(effecttag);
            list.appendTag(effecttag);
        }
        // @todo
        tagCompound.setTag("effects",list);
        result.setTagCompound(tagCompound);
        return result;
    }

    // @todo recipes
    public AddPotionRecipe(String group, ItemStack output, NonNullList<Ingredient> ingredients) {
        super(group, output, ingredients);
    }

    //    public AddPotionRecipe(){
//        super(new ItemStack(ModItems.potionWand), Lists.asList(new ItemStack(ModItems.potionWand),new ItemStack(Items.POTIONITEM), new ItemStack[0]));
//    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int foundWand = 0;
        int foundPotion = 0;
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.potionWand) {
                foundWand++;
            } else if (!stack.isEmpty() && stack.getItem() == Items.POTIONITEM) {
                foundPotion++;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }
        return foundWand == 1 && foundPotion == 1;
    }
}
