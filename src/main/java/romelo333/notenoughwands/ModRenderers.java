package romelo333.notenoughwands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.opengl.GL11;
import romelo333.notenoughwands.blocks.LightRenderer;
import romelo333.notenoughwands.blocks.LightTE;

public final class ModRenderers {

    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(LightTE.class, new LightRenderer());
//        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.lightBlock), new LightItemRenderer());
    }

    public static void renderBillboardQuad(double scale, float vAdd1, float vAdd2) {
        GL11.glPushMatrix();

        rotateToPlayer();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(-scale, -scale, 0); renderer.tex(0, 0 + vAdd1);
        renderer.pos(-scale, +scale, 0); renderer.tex(0, 0+vAdd1+vAdd2);
        renderer.pos(+scale, +scale, 0); renderer.tex(1, 0+vAdd1+vAdd2);
        renderer.pos(+scale, -scale, 0); renderer.tex(1, 0+vAdd1);
        tessellator.draw();
        GL11.glPopMatrix();
    }

    public static void rotateToPlayer() {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    }
}
