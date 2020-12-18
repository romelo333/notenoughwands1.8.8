package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.SoundTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class TeleportationWand extends GenericWand {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header());


    public TeleportationWand() {
        this.usageFactor(2.0f);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);

        if (WandsConfiguration.teleportThroughWalls.get()) {
            list.add(new TranslationTextComponent("message.notenoughwands.teleportation_wand.sneak1").applyTextStyle(TextFormatting.GOLD));
        } else {
            list.add(new TranslationTextComponent("message.notenoughwands.teleportation_wand.sneak2").applyTextStyle(TextFormatting.GOLD));
        }
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
            int distance = WandsConfiguration.maxdist.get();
            boolean gothrough = false;
            if (player.isSneaking()) {
                if (WandsConfiguration.teleportThroughWalls.get()) {
                    gothrough = true;
                }
                distance /= 2;
            }

            Vec3d end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
            RayTraceResult position;
            if (gothrough) {
                position = null;
            } else {
                RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
                position = world.rayTraceBlocks(context);
            }
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
            if (WandsConfiguration.teleportVolume.get() >= 0.01) {
                SoundEvent teleport = WandsModule.TELEPORT_SOUND.get();
                SoundTools.playSound(player.getEntityWorld(), teleport, player.getPosX(), player.getPosY(), player.getPosZ(), WandsConfiguration.teleportVolume.get(), 1.0f);
            }
        }
        return ActionResult.resultPass(stack);
    }
}
