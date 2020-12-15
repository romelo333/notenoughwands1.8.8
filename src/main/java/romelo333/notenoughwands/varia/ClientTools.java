package romelo333.notenoughwands.varia;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class ClientTools {

    public static void renderOutlines(PlayerEntity p, Set<BlockPos> coordinates, int r, int g, int b, float partialTicks) {
//        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
//        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
//        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;
//
//        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
//        Minecraft.getMinecraft().entityRenderer.disableLightmap();
//        GlStateManager.disableDepth();
//        GlStateManager.disableTexture2D();
//        GlStateManager.disableLighting();
//        GlStateManager.disableAlpha();
//        GlStateManager.depthMask(false);
//
//        GlStateManager.pushMatrix();
//        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
//
//        renderOutlines(coordinates, r, g, b, 4);
//
//        GlStateManager.popMatrix();
//
//        Minecraft.getMinecraft().entityRenderer.enableLightmap();
//        GlStateManager.enableTexture2D();
    }

}
