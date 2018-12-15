package romelo333.notenoughwands.blocks;


import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import romelo333.notenoughwands.ModBlocks;
import romelo333.notenoughwands.NotEnoughWands;

import javax.annotation.Nullable;

public class LightBlock extends Block implements BlockEntityProvider {
    public LightBlock() {
        super(FabricBlockSettings.of(Material.PORTAL).hardness(0.0f).build());
        Registry.BLOCK.register(new Identifier(NotEnoughWands.MODID, "lightblock"), this);
    }

    private static final BoundingBox EMPTY = new BoundingBox(0, 0, 0, 0, 0, 0);


    // @todo fabric
//    @Override
//    public BoundingBox getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos) {
//        return EMPTY;
//    }

//    @Override
//    public int quantityDropped(Random rnd) {
//        return 0;
//    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new LightTE(ModBlocks.LIGHT);
    }

    @Override
    public int getLuminance(BlockState var1) {
        return 15;
    }

    @Override
    public VoxelShape getBoundingShape(BlockState var1, BlockView var2, BlockPos var3) {
        return VoxelShapes.empty();
    }


    // @todo fabric
//    @Override
//    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
//    }
//
//
//    @Override
//    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
//        return false;
//    }
//
//    @Override
//    public boolean isOpaqueCube(IBlockState state) {
//        return false;
//    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
//        return true;
//    }


//    @Override
//    @SideOnly(Side.CLIENT)
//    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
//        return true;
//    }

}
