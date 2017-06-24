package romelo333.notenoughwands;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

public class ModSounds {

    public static final String[] REGISTER_SOUND = { "registerSound", "func_187502_a", "a" };

    public static void init(IForgeRegistry<SoundEvent> registry) {
        SoundEvent teleport = new SoundEvent(new ResourceLocation(NotEnoughWands.MODID, "teleport"));
        teleport.setRegistryName(new ResourceLocation(NotEnoughWands.MODID, "teleport"));
        registry.register(teleport);
    }

    // Server side: play a sound to all nearby players
    public static void playSound(World worldObj, SoundEvent sound, double x, double y, double z, double volume, double pitch) {
        SPacketSoundEffect soundEffect = new SPacketSoundEffect(sound, SoundCategory.BLOCKS, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.playerEntities.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)worldObj.playerEntities.get(j);
            double d7 = x - entityplayermp.posX;
            double d8 = y - entityplayermp.posY;
            double d9 = z - entityplayermp.posZ;
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                entityplayermp.connection.sendPacket(soundEffect);
            }
        }
    }


}
