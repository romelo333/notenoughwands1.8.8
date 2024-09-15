package romelo333.notenoughwands.modules.lightwand.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class LightBlock extends Block {
    public LightBlock() {
        super(Properties.of()
                .strength(0.0f, 1.0f)
                .noOcclusion()
                .noLootTable()
                .noCollission()
                .lightLevel(state -> 15));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
