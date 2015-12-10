package romelo333.notenoughwands.blocks;

import net.minecraft.tileentity.TileEntity;

public class LightTE extends TileEntity {
    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }
}
