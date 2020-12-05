package romelo333.notenoughwands.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class LightBlock extends Block {
    public LightBlock() {
        super(Properties.create(Material.PORTAL)
                .hardnessAndResistance(0.0f, 1.0f)
                .notSolid()
                .noDrops()
                .doesNotBlockMovement()
                .lightValue(15));
    }

    private static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LightTE();
    }

//    @Override
    // @todo 1.15
//    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
//        return true;
//    }


//    @Override
    // @todo 1.15
//    @SideOnly(Side.CLIENT)
//    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
//        return true;
//    }

}
