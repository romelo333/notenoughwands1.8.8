package romelo333.notenoughwands.modules.lightwand.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import romelo333.notenoughwands.modules.lightwand.LightModule;

public class LightTE extends BlockEntity {

    public LightTE(BlockPos pos, BlockState state) {
        super(LightModule.TYPE_LIGHT.get(), pos, state);
    }

//
//    @Override
//    public boolean shouldRenderInPass(int pass) {
//        return pass == 1;
//    }
}
