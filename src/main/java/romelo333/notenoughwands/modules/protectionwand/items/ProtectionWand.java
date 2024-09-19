package romelo333.notenoughwands.modules.protectionwand.items;


import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandModule;
import romelo333.notenoughwands.modules.protectionwand.data.ProtectionWandData;
import romelo333.notenoughwands.modules.protectionwand.network.PacketGetProtectedBlockCount;
import romelo333.notenoughwands.modules.protectionwand.network.PacketGetProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.network.ReturnProtectedBlockCountHelper;
import romelo333.notenoughwands.modules.protectionwand.network.ReturnProtectedBlocksHelper;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

import static romelo333.notenoughwands.modules.protectionwand.data.ProtectionWandData.Mode.MODE_PROTECT;
import static romelo333.notenoughwands.modules.protectionwand.data.ProtectionWandData.Mode.MODE_UNPROTECT;

public class ProtectionWand extends GenericWand {

    private final boolean master;

    public ProtectionWand(boolean master) {
        super();
        if (master) {
            this.usageFactor(3.0f);
        } else {
            this.usageFactor(3.0f);
        }
        this.master = master;
    }

    private static long tooltipLastTime = 0;

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, list, flagIn);
        ProtectionWandData data = stack.getOrDefault(ProtectionWandModule.PROTECTIONWAND_DATA, ProtectionWandData.DEFAULT);
        boolean hasid = data.id() != 0;
        ProtectionWandData.Mode mode = data.mode();
        int id = getId(stack);
        if (hasid && id != 0) {
            if ((System.currentTimeMillis() - tooltipLastTime) > 250) {
                tooltipLastTime = System.currentTimeMillis();
                NEWPacketHandler.sendToServer(PacketGetProtectedBlockCount.create(id));
            }
        }
        // @todo 1.15 better tooltips
        list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Mode: " + mode.getDescription()));
        if (master) {
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Master wand"));
        } else {
            if (id != 0) {
                list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Id: " + id));
            }
        }
        if (hasid) {
            list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Number of protected blocks: " + ReturnProtectedBlockCountHelper.count));
        }
        list.add(ComponentFactory.literal("Right click to protect or unprotect a block."));
        showModeKeyDescription(list, "switch mode");
    }

    @Override
    public void toggleMode(Player player, ItemStack stack) {
        ProtectionWandData.Mode mode = getMode(stack).next();
        Tools.notify(player, ComponentFactory.literal("Switched to " + mode.getDescription() + " mode"));
        stack.update(ProtectionWandModule.PROTECTIONWAND_DATA, ProtectionWandData.DEFAULT, data -> data.withMode(mode));
    }

    private ProtectionWandData.Mode getMode(ItemStack stack) {
        return stack.getOrDefault(ProtectionWandModule.PROTECTIONWAND_DATA, ProtectionWandData.DEFAULT).mode();
    }

    public int getId(ItemStack stack) {
        if (master) {
            return -1;
        }
        return stack.getOrDefault(ProtectionWandModule.PROTECTIONWAND_DATA, ProtectionWandData.DEFAULT).id();
    }

    private static long lastTime = 0;

    // @todo 1.20 correct event?
    @Override
    public void renderOverlay(RenderLevelStageEvent evt, Player player, ItemStack wand) {
        if ((System.currentTimeMillis() - lastTime) > 250) {
            lastTime = System.currentTimeMillis();
            NEWPacketHandler.sendToServer(new PacketGetProtectedBlocks());
        }
        if (master) {
            renderOutlines(evt, player, ReturnProtectedBlocksHelper.childBlocks, 30, 30, 200);
        }
        renderOutlines(evt, player, ReturnProtectedBlocksHelper.blocks, 210, 60, 40);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            int id = getOrCreateId(stack, world, protectedBlocks);
            ProtectionWandData.Mode mode = getMode(stack);
            if (mode == MODE_PROTECT) {
                if (!checkUsage(stack, player, 1.0f)) {
                    return InteractionResult.FAIL;
                }
                if (!protectedBlocks.protect(player, world, pos, id)) {
                    return InteractionResult.FAIL;
                }
                registerUsage(stack, player, 1.0f);
            } else if (mode == MODE_UNPROTECT) {
                if (!protectedBlocks.unprotect(player, world, pos, id)) {
                    return InteractionResult.FAIL;
                }
            } else {
                int cnt = protectedBlocks.clearProtections(world, id);
                Tools.notify(player, ComponentFactory.literal("Cleared " + cnt + " protected blocks"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private int getOrCreateId(ItemStack stack, Level world, ProtectedBlocks protectedBlocks) {
        int id = getId(stack);
        if (id == 0) {
            id = protectedBlocks.getNewId();
            int finalId = id;
            stack.update(ProtectionWandModule.PROTECTIONWAND_DATA, ProtectionWandData.DEFAULT, data -> data.withId(finalId));
        }
        return id;
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return !master;
    }


// @todo 1.15
//    @Override
//    public Item getContainerItem() {
//        return this;
//    }


    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (hasCraftingRemainingItem(stack)) {
            ItemStack container = new ItemStack(getCraftingRemainingItem());
            container.set(ProtectionWandModule.PROTECTIONWAND_DATA, stack.get(ProtectionWandModule.PROTECTIONWAND_DATA));
            return container;
        }
        return ItemStack.EMPTY;
    }

}
