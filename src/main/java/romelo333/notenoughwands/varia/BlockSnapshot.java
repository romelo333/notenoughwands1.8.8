package romelo333.notenoughwands.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import org.jspecify.annotations.Nullable;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;

public record BlockSnapshot(Level level, BlockPos pos, BlockState state, @Nullable CompoundTag data) {

    public static BlockSnapshot create(Level level, BlockPos pos) {
        var blockEntity = level.getBlockEntity(pos);
        return new BlockSnapshot(level, pos.immutable(), level.getBlockState(pos),
                blockEntity == null ? null : blockEntity.saveWithoutMetadata(level.registryAccess()));
    }

    public boolean canEdit(Player player) {
        return level.mayInteract(player, pos) && !ProtectedBlocks.getProtectedBlocks(level).isProtected(level, pos);
    }

    public void restore(int flags) {
        level.setBlock(pos, state, flags | Block.UPDATE_ALL);
        var blockEntity = level.getBlockEntity(pos);
        if (data != null && blockEntity != null) {
            blockEntity.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data));
            blockEntity.setChanged();
        }
    }
}
