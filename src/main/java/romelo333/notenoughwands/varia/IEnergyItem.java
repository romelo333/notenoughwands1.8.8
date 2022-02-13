package romelo333.notenoughwands.varia;

import net.minecraft.world.item.ItemStack;

public interface IEnergyItem {
    int receiveEnergy(ItemStack container, int maxReceive, boolean simulate);

    int extractEnergy(ItemStack container, int maxExtract, boolean simulate);

    int getEnergyStored(ItemStack container);

    int getMaxEnergyStored(ItemStack container);
}