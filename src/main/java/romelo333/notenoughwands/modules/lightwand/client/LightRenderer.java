package romelo333.notenoughwands.modules.lightwand.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.client.AbstractDynamicBakedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.lightwand.blocks.LightTE;
import romelo333.notenoughwands.varia.WandRenderTypes;

public class LightRenderer implements BlockEntityRenderer<LightTE, BlockEntityRenderState> {

    private static final Identifier LIGHT = Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "block/light");

    public LightRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        var sprite = AbstractDynamicBakedModel.getTexture(LIGHT);
        poses.pushPose();
        poses.translate(0.5, 0.5, 0.5);
        poses.rotate(camera.orientation);
        collector.submitCustomGeometry(poses, WandRenderTypes.GHOST_BLOCKS, (pose, vertices) -> {
            vertices.addVertex(pose, -0.25f, -0.25f, 0).setUv(sprite.getU0(), sprite.getV1()).setColor(0x80FFFFFF);
            vertices.addVertex(pose, 0.25f, -0.25f, 0).setUv(sprite.getU1(), sprite.getV1()).setColor(0x80FFFFFF);
            vertices.addVertex(pose, 0.25f, 0.25f, 0).setUv(sprite.getU1(), sprite.getV0()).setColor(0x80FFFFFF);
            vertices.addVertex(pose, -0.25f, 0.25f, 0).setUv(sprite.getU0(), sprite.getV0()).setColor(0x80FFFFFF);
        });
        poses.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(LightModule.TYPE_LIGHT.get(), LightRenderer::new);
    }
}
