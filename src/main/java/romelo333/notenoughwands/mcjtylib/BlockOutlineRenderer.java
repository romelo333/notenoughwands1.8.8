package romelo333.notenoughwands.mcjtylib;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class BlockOutlineRenderer {

    /**
     * This method translates GL state relative to player position
     */
    public static void renderHilightedBlock(BlockPos c, float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();

        ClientPlayerEntity p = mc.player;
        double doubleX = p.prevRenderX + (p.x - p.prevRenderX) * partialTicks;
        double doubleY = p.prevRenderY + (p.y - p.prevRenderY) * partialTicks;
        double doubleZ = p.prevRenderZ + (p.z - p.prevRenderZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.color3f(1.0f, 0, 0);
        GlStateManager.lineWidth(3);
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        float mx = c.getX();
        float my = c.getY();
        float mz = c.getZ();
        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
        RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, 1.0f, 0.0f, 0.0f, 1.0f);

        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }

    /**
     * This method translates GL state relative to player position
     */
    public static void renderOutlines(PlayerEntity p, Set<BlockPos> coordinates, int r, int g, int b, float partialTicks) {
        double doubleX = p.prevRenderX + (p.x - p.prevRenderX) * partialTicks;
        double doubleY = p.prevRenderY + (p.y - p.prevRenderY) * partialTicks;
        double doubleZ = p.prevRenderZ + (p.z - p.prevRenderZ) * partialTicks;

        // @todo fabric
        GuiLighting.disable(); //        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        MinecraftClient.getInstance().worldRenderer.method_3187();//        MinecraftClient.getInstance().entityRenderer.disableLightmap();


        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        renderOutlines(coordinates, r, g, b, 4);

        GlStateManager.popMatrix();

        // @todo fabric
        MinecraftClient.getInstance().worldRenderer.method_3180();
//        MinecraftClient.getInstance().entityRenderer.enableLightmap();

        GlStateManager.enableTexture();
        GlStateManager.enableDepthTest();
    }


    /**
     * This method expects the GL state matrix to be translated to relative player position already
     * (player.lastTickPos + (player.pos - player.lastTickPos)* partialTicks)
     */
    public static void renderOutlines(Set<BlockPos> coordinates, int r, int g, int b, int thickness) {
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.getBufferBuilder();
        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

//        GlStateManager.color(r / 255.0f, g / 255.0f, b / 255.0f);
        GL11.glLineWidth(thickness);

        for (BlockPos coordinate : coordinates) {
            float x = coordinate.getX();
            float y = coordinate.getY();
            float z = coordinate.getZ();

            renderHighLightedBlocksOutline(buffer, x, y, z, r / 255.0f, g / 255.0f, b / 255.0f, 1.0f); // .02f
        }
        tessellator.draw();
    }

    public static void renderHighLightedBlocksOutline(BufferBuilder buffer, float mx, float my, float mz, float r, float g, float b, float a) {
        buffer.vertex(mx, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz).color(r, g, b, a).next();

        buffer.vertex(mx, my+1, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz).color(r, g, b, a).next();

        buffer.vertex(mx+1, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz).color(r, g, b, a).next();

        buffer.vertex(mx, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz+1).color(r, g, b, a).next();
    }



    /**
     * This method expects the GL state matrix to be translated to relative player position already
     * (player.lastTickPos + (player.pos - player.lastTickPos)* partialTicks)
     */
    public static void renderBoxOutline(BlockPos pos) {
        // @todo fabric
        GuiLighting.disable(); //        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        MinecraftClient.getInstance().worldRenderer.method_3187();//        MinecraftClient.getInstance().entityRenderer.disableLightmap();

        GlStateManager.disableTexture();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        GlStateManager.lineWidth(2);
        GlStateManager.color3f(1, 1, 1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();
        float mx = pos.getX();
        float my = pos.getY();
        float mz = pos.getZ();
        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
        RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, .9f, .7f, 0, 1);

        tessellator.draw();

        // @todo fabric
        MinecraftClient.getInstance().worldRenderer.method_3180(); //        MinecraftClient.getInstance().entityRenderer.enableLightmap();
        GlStateManager.enableTexture();
    }

    /**
     * This method translates GL state relative to player position
     */
    public static void renderHighlightedBlocks(ClientPlayerEntity p, BlockPos base, Set<BlockPos> coordinates, Identifier texture, float partialTicks) {
        double doubleX = p.prevRenderX + (p.x - p.prevRenderX) * partialTicks;
        double doubleY = p.prevRenderY + (p.y - p.prevRenderY) * partialTicks;
        double doubleZ = p.prevRenderZ + (p.z - p.prevRenderZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBufferBuilder();

        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);

        buffer.begin(GL11.GL_QUADS, VertexFormats.field_1586);
//        tessellator.setColorRGBA(255, 255, 255, 64);
//        tessellator.setBrightness(240);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (BlockPos coordinate : coordinates) {
            float x = base.getX() + coordinate.getX();
            float y = base.getY() + coordinate.getY();
            float z = base.getZ() + coordinate.getZ();
            Vec3d offs = new Vec3d(x, y, z);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.UP.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.DOWN.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.NORTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.SOUTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.WEST.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, Direction.EAST.ordinal(), 1.1f, -0.05f, offs);
        }
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.disableTexture();
        GlStateManager.color3f(.5f, .3f, 0);
        GlStateManager.lineWidth(2);

        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

        for (BlockPos coordinate : coordinates) {
            RenderHelper.renderHighLightedBlocksOutline(buffer,
                    base.getX() + coordinate.getX(), base.getY() + coordinate.getY(), base.getZ() + coordinate.getZ(),
                    .5f, .3f, 0f, 1.0f);
        }
        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }



}
