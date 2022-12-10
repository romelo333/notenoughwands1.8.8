package romelo333.notenoughwands.modules.lightwand.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;

public class LightBlock extends Block implements EntityBlock {
    public LightBlock() {
        super(Properties.of(Material.PORTAL)
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LightTE(pPos, pState);
    }
}
