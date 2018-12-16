package romelo333.notenoughwands.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import romelo333.notenoughwands.proxy.ClientProxy;

@Mixin(MinecraftClient.class)
public class ClientTickMixin {

    @Inject(at = @At("HEAD"), method = "method_1508()V")
    private void method_1508(CallbackInfo info) {
        ClientProxy.checkKeys();
    }
}
