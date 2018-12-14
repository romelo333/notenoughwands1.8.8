package romelo333.notenoughwands.Items;


import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.ModSounds;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class TeleportationWand extends GenericWand {

    private float teleportVolume = 1.0f;
    private int maxdist = 30;
    private boolean teleportThroughWalls = true;

    public TeleportationWand() {
        setup("teleportation_wand").xpUsage(4).loot(6);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<TextComponent> list, TooltipOptions b) {
        super.addInformation(stack, player, list, b);
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
        super.initConfig(cfg, 500, 100000, 200, 200000, 100, 500000);
        teleportVolume = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_volume", teleportVolume, "Volume of the teleportation sound (set to 0 to disable)").getDouble();
        maxdist = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdist", maxdist, "Maximum teleportation distance").getInt();
        teleportThroughWalls = cfg.getBoolean(getConfigPrefix() + "_teleportThroughWalls", Config.CATEGORY_WANDS, teleportThroughWalls, "If set to true then sneak-right click will teleport through walls. Otherwise sneak-right click will teleport half distance");
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isRemote) {
            if (!checkUsage(stack, player, 1.0f)) {
                return new TypedActionResult<>(ActionResult.PASS, stack);
            }
            Vec3d lookVec = player.getPosVector();
            Vec3d start = new Vec3d(player.x, player.y + player.getEyeHeight(), player.z);
            int distance = this.maxdist;
            boolean gothrough = false;
            if (player.isSneaking()) {
                if (teleportThroughWalls) {
                    gothrough = true;
                }
                distance /= 2;
            }

            Vec3d end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
            HitResult position = gothrough ? null : world.rayTrace(start, end);
            if (position == null) {
                if (gothrough) {
                    // First check if the destination is safe
                    BlockPos blockPos = new BlockPos(end.x, end.y, end.z);
                    if (!(world.isAir(blockPos) && world.isAir(blockPos.up()))) {
                        Tools.error(player, "You will suffocate if you teleport there!");
                        return new TypedActionResult<>(ActionResult.PASS, stack);
                    }
                }
                player.method_5859(end.x, end.y, end.z);        // @todo fabric: setPositionAndUpdate
            } else {
                BlockPos blockPos = position.getBlockPos();
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                if (world.isAir(blockPos.up()) && world.isAir(blockPos.up(2))) {
                    player.method_5859(x+.5, y + 1, z+.5);        // @todo fabric: setPositionAndUpdate
                } else {
                    switch (position.side) {
                        case DOWN:
                            player.method_5859(x+.5, y - 2, z+.5);  // @todo fabric: setPositionAndUpdate
                            break;
                        case UP:
                            Tools.error(player, "You will suffocate if you teleport there!");
                            return new TypedActionResult<>(ActionResult.PASS, stack);
                        case NORTH:
                            player.method_5859(x+.5, y, z - 1 + .5);    // @todo fabric: setPositionAndUpdate
                            break;
                        case SOUTH:
                            player.method_5859(x+.5, y, z + 1+.5);  // @todo fabric: setPositionAndUpdate
                            break;
                        case WEST:
                            player.method_5859(x - 1+.5, y, z+.5);  // @todo fabric: setPositionAndUpdate
                            break;
                        case EAST:
                            player.method_5859(x + 1+.5, y, z+.5);  // @todo fabric: setPositionAndUpdate
                            break;
                    }
                }
            }
            registerUsage(stack, player, 1.0f);
            if (teleportVolume >= 0.01) {
                SoundEvent teleport = Registry.SOUND_EVENT.get(new Identifier(NotEnoughWands.MODID, "teleport"));
                ModSounds.playSound(player.getEntityWorld(), teleport, player.x, player.y, player.z, teleportVolume, 1.0f);
            }
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }
}
