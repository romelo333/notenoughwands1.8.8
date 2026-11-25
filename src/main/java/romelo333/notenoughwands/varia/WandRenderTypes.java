package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import romelo333.notenoughwands.NotEnoughWands;

public final class WandRenderTypes {

    private static final RenderPipeline PREVIEW_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "pipeline/preview"))
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .build();

    public static final RenderType GHOST_BLOCKS = RenderType.create("notenoughwands_ghost_blocks",
            RenderSetup.builder(PREVIEW_PIPELINE).withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                    .sortOnUpload().createRenderSetup());

    private WandRenderTypes() {
    }

    public static void registerPipelines() {
        RenderPipelines.register(PREVIEW_PIPELINE);
    }
}
