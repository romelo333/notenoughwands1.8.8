package romelo333.notenoughwands.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import romelo333.notenoughwands.proxy.ClientProxy;

@Mixin(MinecraftClient.class)
public class ClientTickMixin {

    @Inject(at = @At("HEAD"), method = "handleInputEvents()V")
    private void handleInputEvents(CallbackInfo info) {
        ClientProxy.checkKeys();
    }
}
