package romelo333.notenoughwands.modules.lightwand.client;


import com.mojang.blaze3d.matrix.MatrixStack;
import mcjty.lib.client.RenderHelper;
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
        RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 1.0f, LIGHT);
//
//        bindTexture(TEXTURE);
//
//        matrixStack.push();
//        GlStateManager.enableRescaleNormal();
//        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
//
//        GlStateManager.enableBlend();
//
//        GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
//        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
//
//        long t = System.currentTimeMillis() % 6;
//        RenderHelper.renderBillboardQuadBright(matrixStack, buffer, 1.0f, LIGHT);
//        ModRenderers.renderBillboardQuad(0.6f, t * (1.0f / 6.0f), (1.0f / 6.0f));
//
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        GlStateManager.popMatrix();
    }

    public static void register() {
        ClientRegistry.bindTileEntityRenderer(LightModule.TYPE_LIGHT.get(), LightRenderer::new);
    }
}
