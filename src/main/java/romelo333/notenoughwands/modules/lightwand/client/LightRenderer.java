package romelo333.notenoughwands.modules.lightwand.client;


import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.lightwand.blocks.LightTE;

public class LightRenderer extends TileEntityRenderer<LightTE> {

    public static final ResourceLocation LIGHT = new ResourceLocation(NotEnoughWands.MODID, "block/light");

    public LightRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(LightTE tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.5f, LIGHT, RenderSettings.builder()
                .color(255, 255, 255)
                .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
                .alpha(128)
                .build());
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(LightModule.TYPE_LIGHT.get(), LightRenderer::new);
    }
}
