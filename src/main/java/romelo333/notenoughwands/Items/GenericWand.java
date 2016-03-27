package romelo333.notenoughwands.Items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.varia.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import net.minecraft.client.entity.EntityClientPlayerMP;

@Optional.InterfaceList({
        @Optional.Interface(iface = "cofh.api.energy.IEnergyContainerItem", modid = "CoFHAPI")})
public class GenericWand extends Item implements cofh.api.energy.IEnergyContainerItem {
    protected int needsxp = 0;
    protected int needsrf = 0;
    protected int maxrf = 0;
    protected int availability = AVAILABILITY_NORMAL;

    protected int lootRarity = 10;

    public static int AVAILABILITY_NOT = 0;
    public static int AVAILABILITY_CREATIVE = 1;
    public static int AVAILABILITY_ADVANCED = 2;
    public static int AVAILABILITY_NORMAL = 3;

    private static List<GenericWand> wands = new ArrayList<GenericWand>();

    @SideOnly(Side.CLIENT)
    public void registerModel() {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(NotEnoughWands.MODID + ":" + getUnlocalizedName().substring(5), "inventory"));
    }

    // Check if a given block can be picked up.
    public static double checkPickup(EntityPlayer player, World world, BlockPos pos, Block block, float maxHardness, Map<String, Double> blacklisted) {
        IBlockState state = world.getBlockState(pos);
        float hardness = block.getBlockHardness(state, world, pos);
        if (hardness > maxHardness){
            Tools.error(player, "This block is to hard to take!");
            return -1.0f;
        }
        if (!block.canEntityDestroy(state, world, pos, player)){
            Tools.error(player, "You are not allowed to take this block!");
            return -1.0f;
        }
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        if (protectedBlocks.isProtected(world, pos)) {
            Tools.error(player, "This block is protected. You cannot take it!");
            return -1.0f;
        }

        double cost = 1.0f;
        String unlocName = block.getUnlocalizedName();
        if (blacklisted.containsKey(unlocName)) {
            cost = blacklisted.get(unlocName);
        }
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to take this block");
            return -1.0f;
        }
        return cost;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        super.addInformation(stack, player, list, b);
        if (needsrf > 0) {
            list.add(TextFormatting.GREEN+"Energy: " + getEnergyStored(stack) + " / " + getMaxEnergyStored(stack));
        }
    }

    protected GenericWand setup(String name) {
        if (availability > 0) {
            setMaxStackSize(1);
            setNoRepair();
            setUnlocalizedName(name);
            setCreativeTab(NotEnoughWands.tabNew);
            GameRegistry.registerItem(this, name);
            wands.add(this);
        }
        return this;
    }

    GenericWand xpUsage(int xp) {
        this.needsxp = xp;
        return this;
    }

    GenericWand rfUsage(int maxrf, int rf) {
        this.maxrf = maxrf;
        this.needsrf = rf;
        return this;
    }

    GenericWand durabilityUsage(int maxdurability) {
        setMaxDamage(maxdurability);
        return this;
    }

    GenericWand loot(int rarity) {
        lootRarity = rarity;
        return this;
    }

    GenericWand availability(int availability) {
        this.availability = availability;
        return this;
    }

    public void initConfig(Configuration cfg) {
        needsxp = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
        needsrf = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
        maxrf = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
        setMaxDamage(cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt());
        availability = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_availability", availability, "Is this wand available? (0=no, 1=not craftable, 2=craftable advanced, 3=craftable normal)").getInt();
        lootRarity = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_lootRarity", lootRarity, "How rare should this wand be in chests? Lower is more rare (0 is not in chests)").getInt();
    }

    //------------------------------------------------------------------------------

    protected boolean checkUsage(ItemStack stack, EntityPlayer player, float difficultyScale) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        if (needsxp > 0) {
            int experience = Tools.getPlayerXP(player) - (int)(needsxp * difficultyScale);
            if (experience <= 0) {
                Tools.error(player, "Not enough experience!");
                return false;
            }
        }
        if (isDamageable()) {
            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                Tools.error(player, "This wand can no longer be used!");
                return false;
            }
        }
        if (needsrf > 0) {
            if (getEnergyStored(stack) < (int)(needsrf * difficultyScale)) {
                Tools.error(player, "Not enough energy to use this wand!");
                return false;
            }
        }
        return true;
    }

    protected void registerUsage(ItemStack stack, EntityPlayer player, float difficultyScale) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        if (needsxp > 0) {
            Tools.addPlayerXP(player, -(int) (needsxp * difficultyScale));
        }
        if (isDamageable()) {
            stack.damageItem(1, player);
        }
        if (needsrf > 0) {
            extractEnergy(stack, (int) (needsrf * difficultyScale), false);
        }
    }

    public void toggleMode(EntityPlayer player, ItemStack stack) {
    }

    //------------------------------------------------------------------------------

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        for (GenericWand wand : wands) {
            wand.registerModel();
        }
    }

    //------------------------------------------------------------------------------

    public static void setupCrafting() {
        for (GenericWand wand : wands) {
            if (wand.availability == AVAILABILITY_NORMAL) {
                wand.setupCraftingInt(ModItems.wandCore);
            } else if (wand.availability == AVAILABILITY_ADVANCED) {
                wand.setupCraftingInt(ModItems.advancedWandCore);
            }
        }
    }

    public static void setupConfig(Configuration cfg) {
        for (GenericWand wand : wands) {
            wand.initConfig(cfg);
        }
    }

    protected void setupCraftingInt(Item wandcore) {
    }

    //------------------------------------------------------------------------------

    public static void setupChestLoot() {
        for (GenericWand wand : wands) {
            wand.setupChestLootInt();
        }
    }

    public void setupChestLootInt() {
        if (lootRarity > 0 && availability > 0) {
//            setupChestLootInt(ChestGenHooks.DUNGEON_CHEST);
//            setupChestLootInt(ChestGenHooks.MINESHAFT_CORRIDOR);
//            setupChestLootInt(ChestGenHooks.PYRAMID_DESERT_CHEST);
//            setupChestLootInt(ChestGenHooks.PYRAMID_JUNGLE_CHEST);
//            setupChestLootInt(ChestGenHooks.STRONGHOLD_CORRIDOR);
//            setupChestLootInt(ChestGenHooks.VILLAGE_BLACKSMITH);
            // @todo
        }
    }

    private void setupChestLootInt(String category) {
//        ChestGenHooks chest = ChestGenHooks.getInfo(category);
//        chest.addItem(new WeightedRandomChestContent(this, 0, 1, 1, lootRarity));
        // @todo
    }

    //------------------------------------------------------------------------------

    @SideOnly(Side.CLIENT)
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayerSP player, ItemStack wand) {

    }

    protected static void renderOutlines(RenderWorldLastEvent evt, EntityPlayerSP p, Set<BlockPos> coordinates, int r, int g, int b) {
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * evt.getPartialTicks();
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * evt.getPartialTicks();
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * evt.getPartialTicks();

        GlStateManager.pushAttrib();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        renderOutlines(coordinates, r, g, b, 4);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private static void renderOutlines(Set<BlockPos> coordinates, int r, int g, int b, int thickness) {
        RenderHelper.enableStandardItemLighting();

        Tessellator tessellator = Tessellator.getInstance();

        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        GlStateManager.color(r / 255.0f, g / 255.0f, b / 255.0f);
        GL11.glLineWidth(thickness);

        for (BlockPos coordinate : coordinates) {
            float x = coordinate.getX();
            float y = coordinate.getY();
            float z = coordinate.getZ();

            renderBlockOutline(buffer, x, y, z, .0f); // .02f
        }
        tessellator.draw();

        RenderHelper.disableStandardItemLighting();
    }

    private static void renderBlockOutline(VertexBuffer buffer, float mx, float my, float mz, float o) {
        buffer.pos(mx - o, my - o, mz - o).endVertex();
        buffer.pos(mx + 1 + o, my - o, mz - o).endVertex();
        buffer.pos(mx - o, my - o, mz - o).endVertex();
        buffer.pos(mx - o, my + 1 + o, mz - o).endVertex();
        buffer.pos(mx - o, my - o, mz - o).endVertex();
        buffer.pos(mx - o, my - o, mz + 1 + o).endVertex();
        buffer.pos(mx + 1 + o, my + 1 + o, mz + 1 + o).endVertex();
        buffer.pos(mx - o, my + 1 + o, mz + 1 + o).endVertex();
        buffer.pos(mx + 1 + o, my + 1 + o, mz + 1 + o).endVertex();
        buffer.pos(mx + 1 + o, my - o, mz + 1 + o).endVertex();
        buffer.pos(mx + 1 + o, my + 1 + o, mz + 1 + o).endVertex();
        buffer.pos(mx + 1 + o, my + 1 + o, mz - o).endVertex();

        buffer.pos(mx - o, my + 1 + o, mz - o).endVertex();
        buffer.pos(mx - o, my + 1 + o, mz + 1 + o).endVertex();
        buffer.pos(mx - o, my + 1 + o, mz - o).endVertex();
        buffer.pos(mx + 1 + o, my + 1 + o, mz - o).endVertex();

        buffer.pos(mx + 1 + o, my - o, mz - o).endVertex();
        buffer.pos(mx + 1 + o, my - o, mz + 1 + o).endVertex();
        buffer.pos(mx + 1 + o, my - o, mz - o).endVertex();
        buffer.pos(mx + 1 + o, my + 1 + o, mz - o).endVertex();

        buffer.pos(mx, my, mz + 1 + o).endVertex();
        buffer.pos(mx + 1 + o, my, mz + 1 + o).endVertex();
        buffer.pos(mx, my, mz + 1 + o).endVertex();
        buffer.pos(mx, my + 1 + o, mz + 1 + o).endVertex();
    }



    //------------------------------------------------------------------------------

    @Override
    @Optional.Method(modid = "CoFHAPI")
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (maxrf <= 0) {
            return 0;
        }

        if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
            return 0;
        }
        int energy = container.getTagCompound().getInteger("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.needsrf, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            container.getTagCompound().setInteger("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI")
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (maxrf <= 0) {
            return 0;
        }

        if (container.getTagCompound() == null) {
            container.setTagCompound(new NBTTagCompound());
        }
        int energy = container.getTagCompound().getInteger("Energy");
        int energyReceived = Math.min(maxrf - energy, Math.min(this.maxrf, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.getTagCompound().setInteger("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    @Optional.Method(modid = "CoFHAPI")
    public int getEnergyStored(ItemStack container) {
        if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
            return 0;
        }
        return container.getTagCompound().getInteger("Energy");
    }

    @Override
    @Optional.Method(modid = "CoFHAPI")
    public int getMaxEnergyStored(ItemStack container) {
        return maxrf;
    }
}
