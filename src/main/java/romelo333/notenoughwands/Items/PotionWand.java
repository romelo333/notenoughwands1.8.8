package romelo333.notenoughwands.Items;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

import java.util.ArrayList;
import java.util.List;

public class PotionWand extends GenericWand {
    private boolean allowPassive = true;
    private boolean allowHostile = true;
    private float difficultyMult = 0.0f;
    private float diffcultyAdd = 1.0f;

    public PotionWand() {
        setup("freezing_wand").xpUsage(10).availability(AVAILABILITY_ADVANCED).loot(4);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        allowPassive =  cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_allowPassive", allowPassive, "Allow freeze passive mobs").getBoolean();
        allowHostile =  cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_allowHostile", allowHostile, "Allow freeze hostile mobs").getBoolean();
        difficultyMult = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_difficultyMult", difficultyMult, "Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
        diffcultyAdd = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_diffcultyAdd", diffcultyAdd, "Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        list.add("Right click on creature to freeze creature.");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {

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

    private void freezePlayer(EntityPlayer player){
        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 4));
    }

    public static List<EntityLivingBase> frozen = new ArrayList<>();
    private void freezeMob(EntityLivingBase mob){
//        mob.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 4));
        frozen.add(mob);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.worldObj.isRemote) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityLivingBase instanceof EntityPlayer) {
                    freezePlayer((EntityPlayer)entityLivingBase);
                    return true;
                }
                if ((!allowHostile) && entityLivingBase instanceof IMob) {
                    Tools.error(player, "It is not possible to freeze hostile mobs with this wand!");
                    return true;
                }
                if ((!allowPassive) && !(entityLivingBase instanceof IMob)) {
                    Tools.error(player, "It is not possible to freeze passive mobs with this wand!");
                    return true;
                }

                float difficultyScale = entityLivingBase.getMaxHealth() * difficultyMult + diffcultyAdd;
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                freezeMob(entityLivingBase);
                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "is ", "sw ", "  w", 's', Blocks.slime_block, 'i', Blocks.packed_ice, 'w', wandcore);
    }
}
