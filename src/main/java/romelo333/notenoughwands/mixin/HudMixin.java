package romelo333.notenoughwands.mixin;

import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import romelo333.notenoughwands.proxy.ClientProxy;

@Mixin(WorldRenderer.class)
public class HudMixin {

//    @Inject(at = @At("RETURN"), method = "renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/class_856;F)V")

    @Inject(
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
                    args = "ldc=blockentities"
            ),
            method = "renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/VisibleRegion;F)V"
    )
    public void renderEntities(Entity var1, VisibleRegion var2, float var3, CallbackInfo info) {
        ClientProxy.renderWorldLastEvent();
    }
}
