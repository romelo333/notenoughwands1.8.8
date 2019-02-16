package romelo333.notenoughwands.items;


import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.ModBlocks;

import java.util.List;

public class IlluminationWand extends GenericWand {
    public IlluminationWand() {
        super(100); // @todo fabric
        setup("illumination_wand").xpUsage(3).loot(6);
    }

    @Override
    protected void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
    }

    @Override
    public void buildTooltip(ItemStack stack, World player, List<TextComponent> list, TooltipOptions b) {
        super.buildTooltip(stack, player, list, b);
        list.add(new StringTextComponent("Right click on block to spawn light."));
        list.add(new StringTextComponent("Right click on light to remove it again."));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction side = context.getFacing();

        ItemStack stack = context.getItemStack();
        if (!world.isClient) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == ModBlocks.lightBlock) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);  // Is this right? @todo fabric: was setBlockToAir
                return ActionResult.SUCCESS;
            }

            if (!world.isAir(pos.offset(side))) {
                return ActionResult.SUCCESS;
            }

            if (!checkUsage(stack, player, 1.0f)) {
                return ActionResult.SUCCESS;
            }

            world.setBlockState(pos.offset(side), ModBlocks.lightBlock.getDefaultState(), 3);

            registerUsage(stack, player, 1.0f);
        }
        return ActionResult.SUCCESS;
    }
}
