package romelo333.notenoughwands.chiselbits;

import mod.chiselsandbits.api.ChiselsAndBitsAddon;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.IChiselsAndBitsAddon;
import romelo333.notenoughwands.NotEnoughWands;

@ChiselsAndBitsAddon
public class ChiselAndBitsSupport implements IChiselsAndBitsAddon {
    @Override
    public void onReadyChiselsAndBits(IChiselAndBitsAPI api) {
        NotEnoughWands.logger.info("Detected Chisel & Bits, enabling support");
        NotEnoughWands.chiselAndBitsAPI = api;
    }
}
