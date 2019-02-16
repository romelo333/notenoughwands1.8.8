package romelo333.notenoughwands.blocks;


import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.loot.context.LootContext;
import romelo333.notenoughwands.ModBlocks;
import romelo333.notenoughwands.NotEnoughWands;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class LightBlock extends Block implements BlockEntityProvider {
    public LightBlock() {
        super(FabricBlockSettings.of(Material.PORTAL).hardness(0.0f).collidable(false).build());
        Registry.BLOCK.register(new Identifier(NotEnoughWands.MODID, "lightblock"), this);
    }

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
    public VoxelShape getCollisionShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, VerticalEntityPosition verticalEntityPosition_1) {
        return super.getCollisionShape(blockState_1, blockView_1, blockPos_1, verticalEntityPosition_1);
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return VoxelShapes.fullCube();
    }
//    @Override
//    public VoxelShape getBoundingShape(BlockState var1, BlockView var2, BlockPos var3) {
//        return VoxelShapes.fullCube();
//    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState var1, LootContext.Builder var2) {
        return Collections.emptyList();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
