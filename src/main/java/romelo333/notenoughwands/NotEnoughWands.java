package romelo333.notenoughwands;

import mcjty.lib.base.ModBase;
import mcjty.lib.proxy.IProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import romelo333.notenoughwands.setup.ModSetup;

@Mod(modid = NotEnoughWands.MODID, name="Not Enough Wands",
        dependencies =
                    "required-after:mcjtylib_ng@[" + NotEnoughWands.MIN_MCJTYLIB_VER + ",);" +
                    "after:forge@[" + NotEnoughWands.MIN_FORGE11_VER + ",);" +
                    "after:redstoneflux@[" + NotEnoughWands.MIN_COFH_VER + ",)",
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = NotEnoughWands.VERSION)
public class NotEnoughWands implements ModBase {
    public static final String MODID = "notenoughwands";
    public static final String VERSION = "1.7.3";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_COFH_VER = "2.0.0";
    public static final String MIN_MCJTYLIB_VER = "3.1.0";

    @SidedProxy(clientSide="romelo333.notenoughwands.setup.ClientProxy", serverSide="romelo333.notenoughwands.setup.ServerProxy")
    public static IProxy proxy;
    public static ModSetup setup = new ModSetup();

    @Mod.Instance("NotEnoughWands")
    public static NotEnoughWands instance;

    /**
     * Run before anything else. Read your config, create blocks, items, etc, and
     * register them with the GameRegistry.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        setup.preInit(e);
        proxy.preInit(e);
    }

    /**
     * Do your mod setup. Build whatever data structures you care about. Register recipes.
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        setup.init(e);
        proxy.init(e);
    }

    /**
     * Handle interaction with other mods, complete your setup based on this.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        setup.postInit(e);
        proxy.postInit(e);
    }

    @Override
    public String getModId() {
        return NotEnoughWands.MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookindex, String page) {
        // @todo
    }
}
