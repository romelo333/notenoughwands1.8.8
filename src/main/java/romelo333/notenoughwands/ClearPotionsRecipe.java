package romelo333.notenoughwands;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ClearPotionsRecipe /*extends ShapelessRecipes*/ {
//
//    @Override
//    public ItemStack getCraftingResult(InventoryCrafting inv) {
//        ItemStack potion = ItemStack.EMPTY;
//        ItemStack wand = ItemStack.EMPTY;
//        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
//            ItemStack stack = inv.getStackInSlot(i);
//            if (!stack.isEmpty() && stack.getItem() == ModItems.potionWand) {
//                wand = stack;
//            } else if (!stack.isEmpty() && stack.getItem() == Items.GLASS_BOTTLE) {
//                potion = stack;
//            }
//        }
//
//        ItemStack result = super.getCraftingResult(inv);
//        NBTTagCompound tagCompound = wand.getTagCompound();
//        if (tagCompound==null){
//            tagCompound=new NBTTagCompound();
//        }
//        tagCompound= tagCompound.copy();
//        NBTTagList list = new NBTTagList();
//        tagCompound.setTag("effects",list);
//        result.setTagCompound(tagCompound);
//        return result;
//    }
//
//    // @todo recipes
//    public ClearPotionsRecipe(String group, ItemStack output, NonNullList<Ingredient> ingredients) {
//        super(group, output, ingredients);
//    }
//
//    //    public ClearPotionsRecipe(){
////        super(new ItemStack(ModItems.potionWand), Lists.asList(new ItemStack(ModItems.potionWand), new ItemStack(Items.GLASS_BOTTLE), new ItemStack[0]));
////    }
//
//    @Override
//    public boolean matches(InventoryCrafting inv, World worldIn) {
//        int foundWand = 0;
//        int foundPotion = 0;
//        for (int i = 0 ; i < inv.getSizeInventory() ; i++) {
//            ItemStack stack = inv.getStackInSlot(i);
//            if (!stack.isEmpty() && stack.getItem() == ModItems.potionWand) {
//                foundWand++;
//            } else if (!stack.isEmpty() && stack.getItem() == Items.GLASS_BOTTLE) {
//                foundPotion++;
//            } else if (!stack.isEmpty()) {
//                return false;
//            }
//        }
//        return foundWand == 1 && foundPotion == 1;
//    }
}
