package romelo333.notenoughwands.Items;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class CapturingWand extends GenericWand {
    private boolean allowPassive = true;
    private boolean allowHostile = true;
    private float difficultyMult = 0.0f;
    private float diffcultyAdd = 1.0f;

    public CapturingWand() {
        setup("capturing_wand").xpUsage(10).availability(AVAILABILITY_ADVANCED).loot(3);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        allowPassive =  cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_allowPassive", allowPassive, "Allow capturing passive mobs").getBoolean();
        allowHostile =  cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_allowHostile", allowHostile, "Allow capturing hostile mobs").getBoolean();
        difficultyMult = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_difficultyMult", difficultyMult, "Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
        diffcultyAdd = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_diffcultyAdd", diffcultyAdd, "Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null) {
            if (tagCompound.hasKey("mob")) {
                String type = tagCompound.getString("type");
                String name = null;
                try {
                    name = Class.forName(type).getSimpleName();
                } catch (ClassNotFoundException e) {
                    name = "?";
                }
                list.add(EnumChatFormatting.GREEN + "Captured mob: " + name);
            }
        }
        list.add("Left click on creature to capture it.");
        list.add("Right click on block to respawn creature.");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NBTTagCompound tagCompound = Tools.getTagCompound(stack);
            if (tagCompound.hasKey("mob")) {
                NBTBase mobCompound = tagCompound.getTag("mob");
                String type = tagCompound.getString("type");
                EntityLivingBase entityLivingBase = createEntity(player, world, type);
                if (entityLivingBase == null) {
                    Tools.error(player, "Something went wrong trying to spawn creature!");
                    return true;
                }
                entityLivingBase.readEntityFromNBT((NBTTagCompound) mobCompound);
                entityLivingBase.setLocationAndAngles(pos.getX()+.5, pos.getY()+1, pos.getZ()+.5, 0, 0);
                tagCompound.removeTag("mob");
                tagCompound.removeTag("type");
                world.spawnEntityInWorld(entityLivingBase);
            } else {
                Tools.error(player, "There is no mob captured in this wand!");
            }
        }
        return true;
    }

    private EntityLivingBase createEntity(EntityPlayer player, World world, String type) {
        EntityLivingBase entityLivingBase;
        try {
            entityLivingBase = (EntityLivingBase) Class.forName(type).getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            entityLivingBase = null;
        }
        return entityLivingBase;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.worldObj.isRemote) {
            if (entity instanceof EntityLivingBase) {
                if (Tools.getTagCompound(stack).hasKey("mob")) {
                    Tools.error(player, "There is already a mob in this wand!");
                    return true;
                }
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityLivingBase instanceof EntityPlayer) {
                    Tools.error(player, "I don't think that player would appreciate being captured!");
                    return true;
                }

                if ((!allowHostile) && entityLivingBase instanceof IMob) {
                    Tools.error(player, "It is not possible to capture hostile mobs with this wand!");
                    return true;
                }
                if ((!allowPassive) && !(entityLivingBase instanceof IMob)) {
                    Tools.error(player, "It is not possible to capture passive mobs with this wand!");
                    return true;
                }

                float difficultyScale = entityLivingBase.getMaxHealth() * difficultyMult + diffcultyAdd;
                System.out.println("difficultyScale = " + difficultyScale);
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                NBTTagCompound tagCompound = new NBTTagCompound();
                entityLivingBase.writeToNBT(tagCompound);
                Tools.getTagCompound(stack).setTag("mob", tagCompound);
                Tools.getTagCompound(stack).setString("type", entity.getClass().getCanonicalName());
                player.worldObj.removeEntity(entity);

                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "dr ", "rw ", "  w", 'r', Items.rotten_flesh, 'd', Items.diamond, 'w', wandcore);
    }
}
