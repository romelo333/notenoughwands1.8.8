package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.CustomRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class ClientTools {

    public static void renderOutlines(PoseStack matrixStack, MultiBufferSource.BufferSource buffer, Set<BlockPos> coordinates, int r, int g, int b) {

        VertexConsumer builder = buffer.getBuffer(CustomRenderTypes.OVERLAY_LINES);

        matrixStack.pushPose();

        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
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
