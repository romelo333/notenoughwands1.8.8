package romelo333.notenoughwands.blocks;


import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import romelo333.notenoughwands.ModRenderers;
import romelo333.notenoughwands.NotEnoughWands;

public class LightRenderer extends BlockEntityRenderer {
    Identifier texture = new Identifier(NotEnoughWands.MODID.toLowerCase(), "textures/blocks/light.png");


    @Override
    public void render(BlockEntity tileEntity, double x, double y, double z, float time, int destroyStage) {
        bindTexture(texture);

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);

        GlStateManager.enableBlend();

        GlStateManager.translatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

        long t = System.currentTimeMillis() % 6;
        ModRenderers.renderBillboardQuad(0.6f, t * (1.0f / 6.0f), (1.0f / 6.0f));

        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.popMatrix();
    }
}
