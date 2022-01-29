package romelo333.notenoughwands.modules.lightwand.client;


import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.CustomRenderTypes;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.client.RenderSettings;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.lightwand.blocks.LightTE;

public class LightRenderer implements BlockEntityRenderer<LightTE> {

    public static final ResourceLocation LIGHT = new ResourceLocation(NotEnoughWands.MODID, "block/light");

    public LightRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(LightTE tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 0.5f, LIGHT, RenderSettings.builder()
                .color(255, 255, 255)
                .renderType(CustomRenderTypes.TRANSLUCENT_LIGHTNING_NOLIGHTMAPS)
                .alpha(128)
                .build());
    }

    public static void register() {
        BlockEntityRenderers.register(LightModule.TYPE_LIGHT.get(), LightRenderer::new);
    }
}
