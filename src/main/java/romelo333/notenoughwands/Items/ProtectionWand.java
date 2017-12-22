package romelo333.notenoughwands.Items;


import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.Config;
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
        if (master) {
            setup("master_protection_wand").xpUsage(0).availability(AVAILABILITY_CREATIVE).loot(0);
        } else {
            setup("protection_wand").xpUsage(50).availability(AVAILABILITY_ADVANCED).loot(1);
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
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        boolean hasid = stack.getTagCompound() != null && stack.getTagCompound().hasKey("id");
        int mode = getMode(stack);
        int id = getId(stack);
        if (hasid && id != 0) {
            if ((System.currentTimeMillis() - tooltipLastTime) > 250) {
                tooltipLastTime = System.currentTimeMillis();
                NEWPacketHandler.INSTANCE.sendToServer(new PacketGetProtectedBlockCount(id));
            }
        }
        list.add(TextFormatting.GREEN + "Mode: " + descriptions[mode]);
        if (master) {
            list.add(TextFormatting.YELLOW + "Master wand");
        } else {
            if (id != 0) {
                list.add(TextFormatting.GREEN + "Id: " + id);
            }
        }
        if (hasid) {
            list.add(TextFormatting.GREEN + "Number of protected blocks: " + ReturnProtectedBlockCountHelper.count);
        }
        list.add("Right click to protect or unprotect a block.");
        showModeKeyDescription(list, "switch mode");
    }

    @Override
    public void toggleMode(EntityPlayer player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, "Switched to " + descriptions[mode] + " mode");
        Tools.getTagCompound(stack).setInteger("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("mode");
    }

    public int getId(ItemStack stack) {
        if (master) {
            return -1;
        }
        return Tools.getTagCompound(stack).getInteger("id");
    }

    private static long lastTime = 0;

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayerSP player, ItemStack wand) {
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
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            int id = getOrCreateId(stack, world, protectedBlocks);
            int mode = getMode(stack);
            if (mode == MODE_PROTECT) {
                if (!checkUsage(stack, player, 1.0f)) {
                    return EnumActionResult.FAIL;
                }
                if (!protectedBlocks.protect(player, world, pos, id)) {
                    return EnumActionResult.FAIL;
                }
                registerUsage(stack, player, 1.0f);
            } else if (mode == MODE_UNPROTECT) {
                if (!protectedBlocks.unprotect(player, world, pos, id)) {
                    return EnumActionResult.FAIL;
                }
            } else {
                int cnt = protectedBlocks.clearProtections(world, id);
                Tools.notify(player, "Cleared " + cnt + " protected blocks");
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private int getOrCreateId(ItemStack stack, World world, ProtectedBlocks protectedBlocks) {
        int id = getId(stack);
        if (id == 0) {
            id = protectedBlocks.getNewId(world);
            Tools.getTagCompound(stack).setInteger("id", id);
        }
        return id;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        if (master) {
        } else {
            // @todo recipes
//            MyGameReg.addRecipe(new ContainerToItemRecipe(new ItemStack[] {
//                    new ItemStack(this), new ItemStack(Items.ENDER_EYE), ItemStackTools.getEmptyStack(),
//                    new ItemStack(Items.ENDER_EYE), new ItemStack(wandcore), ItemStackTools.getEmptyStack(),
//                    ItemStackTools.getEmptyStack(), ItemStackTools.getEmptyStack(), new ItemStack(wandcore)
//            }, 0, new ItemStack(this)));
        }
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !master;
    }

    @Override
    public Item getContainerItem() {
        return this;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (hasContainerItem(stack) && stack.hasTagCompound()) {
            ItemStack container = new ItemStack(getContainerItem());
            container.setTagCompound(stack.getTagCompound().copy());
            return container;
        }
        return ItemStack.EMPTY;
    }

}
