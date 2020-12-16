package romelo333.notenoughwands.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandModule;
import romelo333.notenoughwands.modules.wands.WandsModule;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, NotEnoughWands.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        itemHandheld(WandsModule.WAND_CORE.get(), "item/wand_core");
        itemHandheld(WandsModule.ADVANCED_WAND_CORE.get(), "item/advanced_wand_core");

        itemHandheld(WandsModule.ACCELERATION_WAND.get(), "item/acceleration_wand");
        itemHandheld(WandsModule.CAPTURING_WAND.get(), "item/capturing_wand");
        itemHandheld(WandsModule.TELEPORTATION_WAND.get(), "item/teleportation_wand");
//        itemHandheld(WandsModule.FREEZING_WAND.get(), "item/freezing_wand");
//        itemHandheld(WandsModule.POTION_WAND.get(), "item/potion_wand");

        itemHandheld(BuildingWandsModule.BUILDING_WAND.get(), "item/building_wand");
        itemHandheld(BuildingWandsModule.DISPLACEMENT_WAND.get(), "item/displacement_wand");
        itemHandheld(BuildingWandsModule.MOVING_WAND.get(), "item/moving_wand");
        itemHandheld(BuildingWandsModule.SWAPPING_WAND.get(), "item/swapping_wand");

        itemHandheld(ProtectionWandModule.PROTECTION_WAND.get(), "item/protection_wand");
        itemHandheld(ProtectionWandModule.MASTER_PROTECTION_WAND.get(), "item/master_protection_wand");

        itemHandheld(LightModule.ILLUMINATION_WAND.get(), "item/illumination_wand");

        parentedItem(LightModule.LIGHT_ITEM.get(), "block/light");
    }

    @Override
    public String getName() {
        return "Not Enough Wands Item Models";
    }
}
