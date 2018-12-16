package romelo333.notenoughwands;

import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ModSounds {

    private static SoundEvent teleport = new SoundEvent(new Identifier(NotEnoughWands.MODID, "teleport"));

    public static final String[] REGISTER_SOUND = { "registerSound", "func_187502_a", "a" };

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, new Identifier(NotEnoughWands.MODID, "teleport"), teleport);
    }

    // Server side: play a sound to all nearby players
    public static void playSound(World worldObj, SoundEvent sound, double x, double y, double z, double volume, double pitch) {
        PlaySoundClientPacket soundEffect = new PlaySoundClientPacket(sound, SoundCategory.BLOCK, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.players.size(); ++j) {
            ServerPlayerEntity PlayerEntitymp = (ServerPlayerEntity)worldObj.players.get(j);
            double d7 = x - PlayerEntitymp.x;
            double d8 = y - PlayerEntitymp.y;
            double d9 = z - PlayerEntitymp.z;
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                PlayerEntitymp.networkHandler.sendPacket(soundEffect);
            }
        }
    }


}
