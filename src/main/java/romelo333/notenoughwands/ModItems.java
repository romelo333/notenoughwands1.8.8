package romelo333.notenoughwands;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.Items.*;

public class ModItems {
    public static WandCore wandCore;
    public static AdvancedWandCore advancedWandCore;
    public static TeleportationWand teleportationWand;
    public static BuildingWand buildingWand;
    public static SwappingWand swappingWand;
    public static CapturingWand capturingWand;
    public static IlluminationWand illuminationWand;

    public static void init() {
        wandCore = new WandCore("wandcore");
        advancedWandCore = new AdvancedWandCore("advanced_wandcore");
        teleportationWand = new TeleportationWand();
        buildingWand = new BuildingWand();
        swappingWand = new SwappingWand();
        capturingWand = new CapturingWand();
        illuminationWand = new IlluminationWand();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        wandCore.registerModel();
        advancedWandCore.registerModel();
        GenericWand.setupModels();
    }
}
