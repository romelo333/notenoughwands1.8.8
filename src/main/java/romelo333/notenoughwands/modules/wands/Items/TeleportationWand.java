package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import romelo333.notenoughwands.ModSounds;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.setup.Configuration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class TeleportationWand extends GenericWand {

    private float teleportVolume = 1.0f;
    private int maxdist = 30;
    private boolean teleportThroughWalls = true;

    public TeleportationWand() {
        setup().loot(6).usageFactory(2.0f);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        // @todo 1.15 proper tooltip system
        list.add(new StringTextComponent("Right click to teleport forward"));
        list.add(new StringTextComponent("until a block is hit or maximum"));
        list.add(new StringTextComponent("distance is reached."));
        if (teleportThroughWalls) {
            list.add(new StringTextComponent("Sneak to teleport through walls"));
        } else {
            list.add(new StringTextComponent("Sneak for half distance"));
        }
    }

    @Override
    public void initConfig(Configuration cfg) {
        // @todo 1.15 config
//        teleportVolume = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_volume", (int) teleportVolume, "Volume of the teleportation sound (set to 0 to disable)").getDouble();
//        maxdist = cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxdist", maxdist, "Maximum teleportation distance").getInt();
//        teleportThroughWalls = cfg.getBoolean(getConfigPrefix() + "_teleportThroughWalls", ConfigSetup.CATEGORY_WANDS, teleportThroughWalls, "If set to true then sneak-right click will teleport through walls. Otherwise sneak-right click will teleport half distance");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (!checkUsage(stack, player, 1.0f)) {
                return ActionResult.resultPass(stack);
            }
            Vec3d lookVec = player.getLookVec();
            Vec3d start = new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
            int distance = this.maxdist;
            boolean gothrough = false;
            if (player.isSneaking()) {
                if (teleportThroughWalls) {
                    gothrough = true;
                }
                distance /= 2;
            }

            Vec3d end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
            RayTraceResult position = gothrough ? null : null;// @todo 1.15 world.rayTraceBlocks(start, end);
            if (position == null) {
                if (gothrough) {
                    // First check if the destination is safe
                    BlockPos blockPos = new BlockPos(end.x, end.y, end.z);
                    if (!(world.isAirBlock(blockPos) && world.isAirBlock(blockPos.up()))) {
                        Tools.error(player, "You will suffocate if you teleport there!");
                        return ActionResult.resultPass(stack);
                    }
                }
                player.setPositionAndUpdate(end.x, end.y, end.z);
            } else {
                BlockRayTraceResult result = (BlockRayTraceResult) position;
                BlockPos blockPos = result.getPos();
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                if (world.isAirBlock(blockPos.up()) && world.isAirBlock(blockPos.up(2))) {
                    player.setPositionAndUpdate(x+.5, y + 1, z+.5);
                } else {
                    switch (result.getFace()) {
                        case DOWN:
                            player.setPositionAndUpdate(x+.5, y - 2, z+.5);
                            break;
                        case UP:
                            Tools.error(player, "You will suffocate if you teleport there!");
                            return ActionResult.resultPass(stack);
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
                SoundEvent teleport = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(NotEnoughWands.MODID, "teleport"));
                ModSounds.playSound(player.getEntityWorld(), teleport, player.getPosX(), player.getPosY(), player.getPosZ(), teleportVolume, 1.0f);
            }
        }
        return ActionResult.resultPass(stack);
    }
}
