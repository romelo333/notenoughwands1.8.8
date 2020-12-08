package romelo333.notenoughwands;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public final class ModRenderers {

    public static void renderBillboardQuad(MatrixStack stack, double scale, float vAdd1, float vAdd2) {
        stack.push();

        rotateToPlayer();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-scale, -scale, 0).tex(0, 0 + vAdd1).endVertex();
        buffer.pos(-scale, +scale, 0).tex(0, 0 + vAdd1 + vAdd2).endVertex();
        buffer.pos(+scale, +scale, 0).tex(1, 0 + vAdd1 + vAdd2).endVertex();
        buffer.pos(+scale, -scale, 0).tex(1, 0 + vAdd1).endVertex();
        tessellator.draw();
        stack.pop();
    }

    public static void rotateToPlayer() {
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        // @todo 1.15
//        GlStateManager.rotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//        GlStateManager.rotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    }
}
