package romelo333.notenoughwands.modules.protectionwand.items;


import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.network.PacketGetProtectedBlockCount;
import romelo333.notenoughwands.modules.protectionwand.network.PacketGetProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.network.ReturnProtectedBlockCountHelper;
import romelo333.notenoughwands.modules.protectionwand.network.ReturnProtectedBlocksHelper;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class ProtectionWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_PROTECT = 0;
    public static final int MODE_UNPROTECT = 1;
    public static final int MODE_CLEAR = 2;
    public static final int MODE_LAST = MODE_CLEAR;

    private final boolean master;


    public static final String[] descriptions = new String[] {
            "protect", "unprotect", "clear all"
    };

    public ProtectionWand(boolean master) {
        if (master) {
            this.usageFactor(3.0f);
        } else {
            this.usageFactor(3.0f);
        }
        this.master = master;
    }

    private static long tooltipLastTime = 0;

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, world, list, flagIn);
        boolean hasid = stack.getTag() != null && stack.getTag().contains("id");
        int mode = getMode(stack);
        int id = getId(stack);
        if (hasid && id != 0) {
            if ((System.currentTimeMillis() - tooltipLastTime) > 250) {
                tooltipLastTime = System.currentTimeMillis();
                NEWPacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlockCount(id));
            }
        }
        // @todo 1.15 better tooltips
        list.add(new StringTextComponent(TextFormatting.GREEN + "Mode: " + descriptions[mode]));
        if (master) {
            list.add(new StringTextComponent(TextFormatting.YELLOW + "Master wand"));
        } else {
            if (id != 0) {
                list.add(new StringTextComponent(TextFormatting.GREEN + "Id: " + id));
            }
        }
        if (hasid) {
            list.add(new StringTextComponent(TextFormatting.GREEN + "Number of protected blocks: " + ReturnProtectedBlockCountHelper.count));
        }
        list.add(new StringTextComponent("Right click to protect or unprotect a block."));
        showModeKeyDescription(list, "switch mode");
    }

    @Override
    public void toggleMode(PlayerEntity player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, new StringTextComponent("Switched to " + descriptions[mode] + " mode"));
        stack.getOrCreateTag().putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }

    public int getId(ItemStack stack) {
        if (master) {
            return -1;
        }
        return stack.getOrCreateTag().getInt("id");
    }

    private static long lastTime = 0;

    @Override
    public void renderOverlay(RenderWorldLastEvent evt, PlayerEntity player, ItemStack wand) {
        if ((System.currentTimeMillis() - lastTime) > 250) {
            lastTime = System.currentTimeMillis();
            NEWPacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlocks());
        }
        if (master) {
            renderOutlines(evt, player, ReturnProtectedBlocksHelper.childBlocks, 30, 30, 200);
        }
        renderOutlines(evt, player, ReturnProtectedBlocksHelper.blocks, 210, 60, 40);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            int id = getOrCreateId(stack, world, protectedBlocks);
            int mode = getMode(stack);
            if (mode == MODE_PROTECT) {
                if (!checkUsage(stack, player, 1.0f)) {
                    return ActionResultType.FAIL;
                }
                if (!protectedBlocks.protect(player, world, pos, id)) {
                    return ActionResultType.FAIL;
                }
                registerUsage(stack, player, 1.0f);
            } else if (mode == MODE_UNPROTECT) {
                if (!protectedBlocks.unprotect(player, world, pos, id)) {
                    return ActionResultType.FAIL;
                }
            } else {
                int cnt = protectedBlocks.clearProtections(world, id);
                Tools.notify(player, new StringTextComponent("Cleared " + cnt + " protected blocks"));
            }
        }
        return ActionResultType.SUCCESS;
    }

    private int getOrCreateId(ItemStack stack, World world, ProtectedBlocks protectedBlocks) {
        int id = getId(stack);
        if (id == 0) {
            id = protectedBlocks.getNewId();
            stack.getOrCreateTag().putInt("id", id);
        }
        return id;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !master;
    }


// @todo 1.15
//    @Override
//    public Item getContainerItem() {
//        return this;
//    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (hasContainerItem(stack) && stack.hasTag()) {
            ItemStack container = new ItemStack(getContainerItem());
            container.setTag(stack.getTag().copy());
            return container;
        }
        return ItemStack.EMPTY;
    }

}
