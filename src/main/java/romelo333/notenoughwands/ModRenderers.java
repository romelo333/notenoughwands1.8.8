package romelo333.notenoughwands;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.lwjgl.opengl.GL11;
import romelo333.notenoughwands.blocks.LightRenderer;
import romelo333.notenoughwands.blocks.LightTE;

public final class ModRenderers {

    public static void init() {
        BlockEntityRendererRegistry.INSTANCE.register(LightTE.class, new LightRenderer());
    }

    public static void renderBillboardQuad(double scale, float vAdd1, float vAdd2) {
        GlStateManager.pushMatrix();

        rotateToPlayer();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
        buffer.vertex(-scale, -scale, 0).texture(0.0, 0.0 + vAdd1).next();
        buffer.vertex(-scale, +scale, 0).texture(0.0, 0.0 + vAdd1 + vAdd2).next();
        buffer.vertex(+scale, +scale, 0).texture(1.0, 0.0 + vAdd1 + vAdd2).next();
        buffer.vertex(+scale, -scale, 0).texture(1.0, 0.0 + vAdd1).next();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static void rotateToPlayer() {
        EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderManager();
        GlStateManager.rotatef(-manager.field_4679, 0.0F, 1.0F, 0.0F);  // @todo fabric playerViewY
        GlStateManager.rotatef(manager.field_4677, 1.0F, 0.0F, 0.0F);   // @todo fabric playerViewX
    }
}
