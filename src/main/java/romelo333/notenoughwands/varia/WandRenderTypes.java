package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import romelo333.notenoughwands.NotEnoughWands;

import java.util.Optional;

public final class WandRenderTypes {

    private static final RenderPipeline OUTLINE_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "pipeline/outlines"))
            .withColorTargetState(ColorTargetState.DEFAULT)
            .withDepthStencilState(Optional.empty())
            .build();

    static final RenderType OUTLINES = RenderType.create("notenoughwands_outlines",
            RenderSetup.builder(OUTLINE_PIPELINE).createRenderSetup());

    private static final RenderPipeline PREVIEW_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "pipeline/preview"))
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
            .build();

    private static final OitPipelineSet PREVIEW_OIT = OitPipelineSet.builder(
            "notenoughwands_preview",
            RenderPipeline.builder()
                    .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
                    .withVertexShader("core/position_tex_color")
                    .withFragmentShader(Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "preview"))
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR)
                    .withPrimitiveTopology(PrimitiveTopology.QUADS)).build();

    public static final RenderType GHOST_BLOCKS = RenderType.create("notenoughwands_ghost_blocks",
            RenderSetup.builder(PREVIEW_PIPELINE).withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                    .setOitPipelines(PREVIEW_OIT).sortOnUpload().createRenderSetup());

    private WandRenderTypes() {
    }

    public static void registerPipelines() {
        RenderPipelines.register(OUTLINE_PIPELINE);
        RenderPipelines.register(PREVIEW_PIPELINE);
        RenderPipelines.register(PREVIEW_OIT);
    }
}
