package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mcjty.lib.client.CustomRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Set;

public class ClientTools {

    public static void renderOutlines(MatrixStack matrixStack, IRenderTypeBuffer.Impl buffer, Set<BlockPos> coordinates, int r, int g, int b) {

        IVertexBuilder builder = buffer.getBuffer(CustomRenderTypes.OVERLAY_LINES);

        matrixStack.pushPose();

        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        Matrix4f positionMatrix = matrixStack.last().pose();
        for (BlockPos c : coordinates) {
            mcjty.lib.client.RenderHelper.renderHighLightedBlocksOutline(builder, positionMatrix, c.getX(), c.getY(), c.getZ(), r, g, b, 1.0f);
        }

        matrixStack.popPose();
        RenderSystem.disableDepthTest();
        buffer.endBatch(CustomRenderTypes.OVERLAY_LINES);
    }

}
