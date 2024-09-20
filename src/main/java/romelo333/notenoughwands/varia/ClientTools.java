package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.CustomRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Set;

public class ClientTools {

    public static void renderOutlines(PoseStack matrixStack, MultiBufferSource.BufferSource buffer, Set<BlockPos> coordinates, int r, int g, int b) {

//        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.OVERLAY_LINES);
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());

        matrixStack.pushPose();

        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        for (BlockPos c : coordinates) {
            renderHighLightedBlocksOutline(matrixStack, builder, c.getX(), c.getY(), c.getZ(), r, g, b, 1.0f);
        }

        matrixStack.popPose();
        RenderSystem.disableDepthTest();
        buffer.endBatch(CustomRenderTypes.OVERLAY_LINES);
    }

    private static void renderHighLightedBlocksOutline(PoseStack poseStack, VertexConsumer buffer, float mx, float my, float mz, float r, float g, float b, float a) {
        Matrix4f matrix = poseStack.last().pose();
        buffer.addVertex(matrix, mx, my, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my + 1, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my + 1, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my + 1, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my + 1, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my + 1, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my + 1, mz).setColor(r, g, b, a).setNormal(1, 0, 0);

        buffer.addVertex(matrix, mx, my + 1, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my + 1, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my + 1, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my + 1, mz).setColor(r, g, b, a).setNormal(1, 0, 0);

        buffer.addVertex(matrix, mx + 1, my, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my, mz).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my + 1, mz).setColor(r, g, b, a).setNormal(1, 0, 0);

        buffer.addVertex(matrix, mx, my, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx + 1, my, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
        buffer.addVertex(matrix, mx, my + 1, mz + 1).setColor(r, g, b, a).setNormal(1, 0, 0);
    }


}
