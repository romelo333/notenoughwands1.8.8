package romelo333.notenoughwands.Items;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class TeleportationWand extends GenericWand {

    private float teleportVolume = 1.0f;
    private int maxdist = 30;

    public TeleportationWand() {
        setup("TeleportationWand", "teleportationWand").xpUsage(4).availability(AVAILABILITY_NORMAL).loot(6);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        list.add("Rigth click to teleport forward until a block");
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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (!checkUsage(stack, player, 1.0f)) {
                return stack;
            }
            Vec3 lookVec = player.getLookVec();
            Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            int distance = this.maxdist;
            if (player.isSneaking()) {
                distance /= 2;
            }
            Vec3 end = start.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
            MovingObjectPosition position = world.rayTraceBlocks(start, end);
            if (position == null) {
                player.setPositionAndUpdate(end.xCoord, end.yCoord, end.zCoord);
            } else {
                int x = position.blockX;
                int y = position.blockY;
                int z = position.blockZ;
                if (world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z)) {
                    player.setPositionAndUpdate(x+.5, y + 1, z+.5);
                } else {
                    switch (ForgeDirection.getOrientation(position.sideHit)) {
                        case DOWN:
                            player.setPositionAndUpdate(x+.5, y - 2, z+.5);
                            break;
                        case UP:
                            Tools.error(player, "You will suffocate if you teleport there!");
                            return stack;
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
                        case UNKNOWN:
                            return stack;
                    }
                }
            }
            registerUsage(stack, player, 1.0f);
            if (teleportVolume >= 0.01) {
                ((EntityPlayerMP) player).worldObj.playSoundAtEntity(player, NotEnoughWands.MODID + ":teleport", teleportVolume, 1.0f);
            }
        }
        return stack;
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
