package romelo333.notenoughwands.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;

import java.util.List;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {
    @Shadow @Final private ServerLevel level;

    @Inject(method = "calculateExplodedPositions", at = @At("RETURN"))
    private void filterProtectedBlocks(CallbackInfoReturnable<List<BlockPos>> callback) {
        ProtectedBlocks data = ProtectedBlocks.getProtectedBlocks(level);
        if (data.hasProtections()) {
            callback.getReturnValue().removeIf(pos -> data.isProtected(level, pos));
        }
    }
}
