package romelo333.notenoughwands.setup;


import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import romelo333.notenoughwands.modules.lightwand.client.LightRenderer;

import static romelo333.notenoughwands.modules.lightwand.client.LightRenderer.LIGHT;

public class ClientSetup {

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }
        event.addSprite(LIGHT);
    }

    public static void init(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            LightRenderer.register();
        });
    }

}
