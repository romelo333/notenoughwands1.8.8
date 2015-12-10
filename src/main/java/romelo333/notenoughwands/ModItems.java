package romelo333.notenoughwands;


import romelo333.notenoughwands.Items.*;

public class ModItems {
    public static WandCore wandCore;
    public static AdvancedWandCore advancedWandCore;
    public static TeleportationWand teleportationWand;

    public static void init() {
        wandCore = new WandCore("WandCore", "wandCore");
        advancedWandCore = new AdvancedWandCore("AdvancedWandCore", "advancedWandCore");
        teleportationWand = new TeleportationWand();
    }

    public static void initModels() {
        wandCore.registerModel("wandcore");
    }
}
