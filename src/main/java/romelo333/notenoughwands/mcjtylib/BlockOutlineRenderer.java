package romelo333.notenoughwands.mcjtylib;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;
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
        GlStateManager.translatef(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getVertexBuffer();
        float mx = c.getX();
        float my = c.getY();
        float mz = c.getZ();
        buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
        RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, 1.0f, 0.0f, 0.0f, 1.0f);

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    /**
     * This method translates GL state relative to player position
     */
    public static void renderOutlines(EntityPlayerSP p, Set<BlockPos> coordinates, int r, int g, int b, float partialTicks) {
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        MinecraftClient.getInstance().entityRenderer.disableLightmap();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        renderOutlines(coordinates, r, g, b, 4);

        GlStateManager.popMatrix();

        MinecraftClient.getInstance().entityRenderer.enableLightmap();
        GlStateManager.enableTexture2D();
    }


    /**
     * This method expects the GL state matrix to be translated to relative player position already
     * (player.lastTickPos + (player.pos - player.lastTickPos)* partialTicks)
     */
    public static void renderOutlines(Set<BlockPos> coordinates, int r, int g, int b, int thickness) {
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

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
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx, my+1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx+1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my+1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx+1, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz+1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my+1, mz+1).color(r, g, b, a).endVertex();
    }



    /**
     * This method expects the GL state matrix to be translated to relative player position already
     * (player.lastTickPos + (player.pos - player.lastTickPos)* partialTicks)
     */
    public static void renderBoxOutline(BlockPos pos) {
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        MinecraftClient.getInstance().entityRenderer.disableLightmap();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.glLineWidth(2);
        GlStateManager.color(1, 1, 1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float mx = pos.getX();
        float my = pos.getY();
        float mz = pos.getZ();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, .9f, .7f, 0, 1);

        tessellator.draw();

        MinecraftClient.getInstance().entityRenderer.enableLightmap();
        GlStateManager.enableTexture2D();
    }

    /**
     * This method translates GL state relative to player position
     */
    public static void renderHighlightedBlocks(EntityPlayerSP p, BlockPos base, Set<BlockPos> coordinates, ResourceLocation texture, float partialTicks) {
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
//        tessellator.setColorRGBA(255, 255, 255, 64);
//        tessellator.setBrightness(240);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (BlockPos coordinate : coordinates) {
            float x = base.getX() + coordinate.getX();
            float y = base.getY() + coordinate.getY();
            float z = base.getZ() + coordinate.getZ();
            Vec3d offs = new Vec3d(x, y, z);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.UP.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.DOWN.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.NORTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.SOUTH.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.WEST.ordinal(), 1.1f, -0.05f, offs);
            RenderGlowEffect.addSideFullTexture(buffer, EnumFacing.EAST.ordinal(), 1.1f, -0.05f, offs);
        }
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(.5f, .3f, 0);
        GlStateManager.glLineWidth(2);

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BlockPos coordinate : coordinates) {
            RenderHelper.renderHighLightedBlocksOutline(buffer,
                    base.getX() + coordinate.getX(), base.getY() + coordinate.getY(), base.getZ() + coordinate.getZ(),
                    .5f, .3f, 0f, 1.0f);
        }
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }



}
