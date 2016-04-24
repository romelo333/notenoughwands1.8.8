package romelo333.notenoughwands;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class AddPotionRecipe extends ShapelessRecipes {
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack potion = null;
        ItemStack wand = null;
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem() == ModItems.potionWand) {
                wand = stack;
            } else if (stack != null && stack.getItem() == Items.POTIONITEM) {
                potion = stack;
            }
        }

        ItemStack result = super.getCraftingResult(inv);
        NBTTagCompound tagCompound = wand.getTagCompound();
        if (tagCompound==null){
            tagCompound=new NBTTagCompound();
        }
        tagCompound=(NBTTagCompound)tagCompound.copy();
        NBTTagList list = tagCompound.getTagList("effects", Constants.NBT.TAG_COMPOUND);
//        for (PotionEffect effect : ((ItemPotion) potion.getItem()).getEffects(potion)) {
//            NBTTagCompound effecttag = new NBTTagCompound();
//            effect.writeCustomPotionEffectToNBT(effecttag);
//            list.appendTag(effecttag);
//        }
        // @todo
        tagCompound.setTag("effects",list);
        result.setTagCompound(tagCompound);
        return result;
    }

    public AddPotionRecipe(){
        super(new ItemStack(ModItems.potionWand), Lists.asList(new ItemStack(ModItems.potionWand),new ItemStack(Items.POTIONITEM), new ItemStack[0]));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int foundWand = 0;
        int foundPotion = 0;
        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem() == ModItems.potionWand) {
                foundWand++;
            } else if (stack != null && stack.getItem() == Items.POTIONITEM) {
                foundPotion++;
            } else if (stack != null) {
                return false;
            }
        }
        return foundWand == 1 && foundPotion == 1;
    }
}
