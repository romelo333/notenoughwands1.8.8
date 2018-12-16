package romelo333.notenoughwands.blocks;


import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderLayer;
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
    public VoxelShape getBoundingShape(BlockState var1, BlockView var2, BlockPos var3) {
        return VoxelShapes.fullCube();
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState var1, LootContext.Builder var2) {
        return Collections.emptyList();
    }


    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
