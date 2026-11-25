package romelo333.notenoughwands.setup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.keys.KeyBindings;
import romelo333.notenoughwands.keys.KeyInputHandler;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;
import romelo333.notenoughwands.modules.protectionwand.network.PacketGetProtectedBlocksAroundPlayer;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.varia.ClientTools.RenderContext;
import romelo333.notenoughwands.varia.WandRenderTypes;

public class ClientSetup implements ClientModInitializer {
    private int timer;

    @Override
    public void onInitializeClient() {
        NotEnoughWands.MODULES.initClient();
        KeyBindings.init();
        NEWPacketHandler.registerClientMessages();
        WandRenderTypes.registerPipelines();
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        LevelRenderEvents.COLLECT_SUBMITS.register(this::submitWandOverlay);
    }

    private void submitWandOverlay(LevelRenderContext event) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.getMainHandItem().getItem() instanceof GenericWand wand) {
            var context = new RenderContext(event.poseStack(), event.submitNodeCollector(), event.levelState().cameraRenderState.pos);
            wand.renderOverlay(context, player, player.getMainHandItem());
        }
    }

    private void onClientTick(Minecraft minecraft) {
        if (minecraft.player == null) {
            return;
        }
        KeyInputHandler.onClientTick();
        int interval = ProtectionWandConfiguration.cachedClientSideProtection;
        if (interval >= 0 && --timer <= 0) {
            timer = interval;
            NEWPacketHandler.sendToServer(new PacketGetProtectedBlocksAroundPlayer());
        }
    }
}
