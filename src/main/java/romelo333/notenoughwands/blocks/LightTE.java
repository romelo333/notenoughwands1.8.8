package romelo333.notenoughwands.blocks;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import romelo333.notenoughwands.ModBlocks;

public class LightTE extends BlockEntity {

    public LightTE() {
        super(ModBlocks.LIGHT);
    }

    public LightTE(BlockEntityType<?> var1) {
        super(var1);
    }


    // @todo fabric
//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return pass == 1;
//    }
}
