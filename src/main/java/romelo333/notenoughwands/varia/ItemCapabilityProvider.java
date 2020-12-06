package romelo333.notenoughwands.varia;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import romelo333.notenoughwands.Items.IEnergyItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemCapabilityProvider implements ICapabilityProvider {

    private final ItemStack itemStack;
    private final IEnergyItem item;

    private final LazyOptional<IEnergyStorage> energyStorage = LazyOptional.of(this::createEnergyStorage);

    public ItemCapabilityProvider(ItemStack itemStack, IEnergyItem item) {
        this.itemStack = itemStack;
        this.item = item;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == CapabilityEnergy.ENERGY) {
            return energyStorage.cast();
        }
        return LazyOptional.empty();
    }

    private IEnergyStorage createEnergyStorage() {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return item.receiveEnergy(itemStack, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return item.extractEnergy(itemStack, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return item.getEnergyStored(itemStack);
            }

            @Override
            public int getMaxEnergyStored() {
                return item.getMaxEnergyStored(itemStack);
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }
}
