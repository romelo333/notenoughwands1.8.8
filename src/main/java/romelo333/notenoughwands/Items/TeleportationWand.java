package romelo333.notenoughwands.Items;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class TeleportationWand extends GenericWand {

    private float teleportVolume = 1.0f;
    private int maxdist = 30;

    public TeleportationWand() {
        setup("teleportation_wand").xpUsage(4).availability(AVAILABILITY_NORMAL).loot(6);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        list.add("Right click to teleport forward until a block");
        list.add("is hit or maximum distance is reached. Sneak right");
        list.add("click for half distance");
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        teleportVolume = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_volume", teleportVolume, "Volume of the teleportation sound (set to 0 to disable)").getDouble();
        maxdist = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxdist", maxdist, "Maximum teleportation distance").getInt();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (!checkUsage(stack, player, 1.0f)) {
                return ActionResult.newResult(EnumActionResult.PASS, stack);
            }
            Vec3d lookVec = player.getLookVec();
            Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            int distance = this.maxdist;
            if (player.isSneaking()) {
                distance /= 2;
            }
            Vec3d end = start.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
            RayTraceResult position = world.rayTraceBlocks(start, end);
            if (position == null) {
                player.setPositionAndUpdate(end.xCoord, end.yCoord, end.zCoord);
            } else {
                BlockPos blockPos = position.getBlockPos();
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                if (world.isAirBlock(blockPos.up()) && world.isAirBlock(blockPos.up(2))) {
                    player.setPositionAndUpdate(x+.5, y + 1, z+.5);
                } else {
                    switch (position.sideHit) {
                        case DOWN:
                            player.setPositionAndUpdate(x+.5, y - 2, z+.5);
                            break;
                        case UP:
                            Tools.error(player, "You will suffocate if you teleport there!");
                            return ActionResult.newResult(EnumActionResult.PASS, stack);
                        case NORTH:
                            player.setPositionAndUpdate(x+.5, y, z - 1 + .5);
                            break;
                        case SOUTH:
                            player.setPositionAndUpdate(x+.5, y, z + 1+.5);
                            break;
                        case WEST:
                            player.setPositionAndUpdate(x - 1+.5, y, z+.5);
                            break;
                        case EAST:
                            player.setPositionAndUpdate(x + 1+.5, y, z+.5);
                            break;
                    }
                }
            }
            registerUsage(stack, player, 1.0f);
            if (teleportVolume >= 0.01) {
//                ((EntityPlayerMP) player).worldObj.playSoundAtEntity(player, NotEnoughWands.MODID + ":teleport", teleportVolume, 1.0f);
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this),
                "ee ",
                "ew ",
                "  w",
                'e', Items.ender_pearl, 'w', wandcore);
    }
}
