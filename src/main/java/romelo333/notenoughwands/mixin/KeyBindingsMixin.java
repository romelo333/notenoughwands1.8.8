package romelo333.notenoughwands.mixin;

import net.minecraft.client.settings.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import romelo333.notenoughwands.KeyBindings;

@Mixin(GameOptions.class)
public class KeyBindingsMixin {

    @Inject(at = @At("HEAD"), method = "load()V")
    public void load(CallbackInfo info) {
        KeyBindings.registerBindings((GameOptions)(Object)this);
    }
}
