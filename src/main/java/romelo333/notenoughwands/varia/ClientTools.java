package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.CustomRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class ClientTools {

    public static void renderOutlines(MatrixStack matrixStack, IRenderTypeBuffer.Impl buffer, Set<BlockPos> coordinates, int r, int g, int b) {

        IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.OVERLAY_LINES);

        matrixStack.push();

        Vec3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        Matrix4f positionMatrix = matrixStack.getLast().getMatrix();
        for (BlockPos c : coordinates) {
            mcjty.lib.client.RenderHelper.renderHighLightedBlocksOutline(builder, positionMatrix, c.getX(), c.getY(), c.getZ(), r, g, b, 1.0f);
        }

        matrixStack.pop();
        RenderSystem.disableDepthTest();
        buffer.finish(CustomRenderTypes.OVERLAY_LINES);
    }

}
