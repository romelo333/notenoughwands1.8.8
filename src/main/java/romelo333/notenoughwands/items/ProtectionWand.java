package romelo333.notenoughwands.items;


import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.network.*;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class ProtectionWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_PROTECT = 0;
    public static final int MODE_UNPROTECT = 1;
    public static final int MODE_CLEAR = 2;
    public static final int MODE_LAST = MODE_CLEAR;

    public int blockShowRadius = 10;
    public int maximumProtectedBlocks = 16;

    private final boolean master;


    public static final String[] descriptions = new String[] {
            "protect", "unprotect", "clear all"
    };

    public ProtectionWand(boolean master) {
        super(100);
        if (master) {
            setup("master_protection_wand").xpUsage(0).loot(0);
        } else {
            setup("protection_wand").xpUsage(50).loot(1);
        }
        this.master = master;
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
        blockShowRadius = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_blockShowRadius", blockShowRadius, "How far around the player protected blocks will be hilighted").getInt();
        maximumProtectedBlocks = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maximumProtectedBlocks", master ? 0 : maximumProtectedBlocks, "The maximum number of blocks to protect with this wand (set to 0 for no maximum)").getInt();
    }

    private static long tooltipLastTime = 0;

    @Override
    public void buildTooltip(ItemStack stack, World player, List<Component> list, TooltipContext b) {
        super.buildTooltip(stack, player, list, b);
        boolean hasid = stack.getTag() != null && stack.getTag().containsKey("id");
        int mode = getMode(stack);
        int id = getId(stack);
        if (hasid && id != 0) {
            if ((System.currentTimeMillis() - tooltipLastTime) > 250) {
                tooltipLastTime = System.currentTimeMillis();
                NetworkInit.sendToServer(new PacketGetProtectedBlockCount(id));
            }
        }
        list.add(new TextComponent(ChatFormat.GREEN + "Mode: " + descriptions[mode]));
        if (master) {
            list.add(new TextComponent(ChatFormat.YELLOW + "Master wand"));
        } else {
            if (id != 0) {
                list.add(new TextComponent(ChatFormat.GREEN + "Id: " + id));
            }
        }
        if (hasid) {
            list.add(new TextComponent(ChatFormat.GREEN + "Number of protected blocks: " + ReturnProtectedBlockCountHelper.count));
        }
        list.add(new TextComponent("Right click to protect or unprotect a block."));
        showModeKeyDescription(list, "switch mode");
    }

    @Override
    public void toggleMode(PlayerEntity player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, "Switched to " + descriptions[mode] + " mode");
        Tools.getTagCompound(stack).putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInt("mode");
    }

    public int getId(ItemStack stack) {
        if (master) {
            return -1;
        }
        return Tools.getTagCompound(stack).getInt("id");
    }

    private static long lastTime = 0;

    @Override
    public void renderOverlay(PlayerEntity player, ItemStack wand, float partialTicks) {
        if ((System.currentTimeMillis() - lastTime) > 250) {
            lastTime = System.currentTimeMillis();
            NetworkInit.sendToServer(new PacketGetProtectedBlocks());
        }
        if (master) {
            renderOutlines(player, ReturnProtectedBlocksHelper.childBlocks, 30, 30, 200, partialTicks);
        }
        renderOutlines(player, ReturnProtectedBlocksHelper.blocks, 210, 60, 40, partialTicks);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction side = context.getFacing();

        ItemStack stack = context.getItemStack();
        if (!world.isClient) {
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            int id = getOrCreateId(stack, world, protectedBlocks);
            int mode = getMode(stack);
            if (mode == MODE_PROTECT) {
                if (!checkUsage(stack, player, 1.0f)) {
                    return ActionResult.FAIL;
                }
                if (!protectedBlocks.protect(player, world, pos, id)) {
                    return ActionResult.FAIL;
                }
                registerUsage(stack, player, 1.0f);
            } else if (mode == MODE_UNPROTECT) {
                if (!protectedBlocks.unprotect(player, world, pos, id)) {
                    return ActionResult.FAIL;
                }
            } else {
                int cnt = protectedBlocks.clearProtections(world, id);
                Tools.notify(player, "Cleared " + cnt + " protected blocks");
            }
        }
        return ActionResult.SUCCESS;
    }

    private int getOrCreateId(ItemStack stack, World world, ProtectedBlocks protectedBlocks) {
        int id = getId(stack);
        if (id == 0) {
            id = protectedBlocks.getNewId();
            Tools.getTagCompound(stack).putInt("id", id);
        }
        return id;
    }

    // @todo fabric
//    @Override
//    public boolean hasContainerItem(ItemStack stack) {
//        return !master;
//    }

//    @Override
//    public Item getContainerItem() {
//        return this;
//    }

//    @Override
//    public ItemStack getContainerItem(ItemStack stack) {
//        if (hasContainerItem(stack) && stack.hasTag()) {
//            ItemStack container = new ItemStack(getContainerItem());
//            container.setTag(stack.getTag().copy());
//            return container;
//        }
//        return ItemStack.EMPTY;
//    }

}
