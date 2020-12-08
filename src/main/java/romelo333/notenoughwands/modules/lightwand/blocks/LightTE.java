package romelo333.notenoughwands.modules.lightwand.blocks;

import net.minecraft.tileentity.TileEntity;
import romelo333.notenoughwands.modules.lightwand.LightModule;

public class LightTE extends TileEntity {

    public LightTE() {
        super(LightModule.TYPE_LIGHT.get());
    }

//
//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return pass == 1;
//    }
}
