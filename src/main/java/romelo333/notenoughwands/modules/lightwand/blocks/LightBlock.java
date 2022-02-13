package romelo333.notenoughwands.modules.lightwand.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class LightBlock extends Block implements EntityBlock {
    public LightBlock() {
        super(Properties.of(Material.PORTAL)
                .strength(0.0f, 1.0f)
                .noOcclusion()
                .noDrops()
                .noCollission()
                .lightLevel(state -> 15));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
    //TODO those 2 functions don't exist
    /*
    @Override
    public boolean addHitEffects(BlockState state, Level worldObj, HitResult target, ParticleEngine manager) {
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level world, BlockPos pos, ParticleEngine manager) {
        return true;
    }
 */

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LightTE(pPos, pState);
    }
}
