package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.ModBlocks;

import java.util.List;

public class IlluminationWand extends GenericWand {
    public IlluminationWand() {
        setup("illumination_wand").xpUsage(3).availability(AVAILABILITY_NORMAL).loot(6);
    }

    @Override
    protected void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        list.add("Right click on block to spawn light.");
        list.add("Right click on light to remove it again.");
    }

    @Override
    protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == ModBlocks.lightBlock) {
                world.setBlockToAir(pos);
                return EnumActionResult.SUCCESS;
            }

            if (!world.isAirBlock(pos.offset(side))) {
                return EnumActionResult.SUCCESS;
            }

            if (!checkUsage(stack, player, 1.0f)) {
                return EnumActionResult.SUCCESS;
            }

            world.setBlockState(pos.offset(side), ModBlocks.lightBlock.getDefaultState(), 3);

            registerUsage(stack, player, 1.0f);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "gg ", "gw ", "  w", 'g', Items.GLOWSTONE_DUST, 'w', wandcore);
    }

}
