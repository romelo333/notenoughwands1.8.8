package romelo333.notenoughwands.blocks;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import romelo333.notenoughwands.ModRenderers;
import romelo333.notenoughwands.NotEnoughWands;

@SideOnly(Side.CLIENT)
public class LightRenderer extends TileEntitySpecialRenderer {
    ResourceLocation texture = new ResourceLocation(NotEnoughWands.MODID.toLowerCase(), "textures/blocks/light.png");

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float time, int destroyStage) {
        bindTexture(texture);

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0f, 1.0f, 1.0f);

        GlStateManager.enableBlend();

        GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

        long t = System.currentTimeMillis() % 6;
        ModRenderers.renderBillboardQuad(0.6f, t * (1.0f / 6.0f), (1.0f / 6.0f));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.popMatrix();
    }
}
