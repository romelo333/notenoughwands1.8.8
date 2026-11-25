package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class ClientTools {

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final float PREVIEW_ALPHA = 0.30f;
    private static final float[] QUAD_BRIGHTNESS = { 1.0f, 1.0f, 1.0f, 1.0f };
    private static final int[] QUAD_LIGHT = {
            LightTexture.FULL_BRIGHT, LightTexture.FULL_BRIGHT, LightTexture.FULL_BRIGHT, LightTexture.FULL_BRIGHT
    };

    private ClientTools() {
    }

    public static void renderOutlines(PoseStack poseStack, MultiBufferSource.BufferSource buffer, Set<BlockPos> positions, int r, int g, int b) {
        if (positions.isEmpty()) {
            return;
        }
        VertexConsumer vertices = buffer.getBuffer(WandRenderTypes.OUTLINES);
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        for (BlockPos pos : positions) {
            double x = pos.getX() - camera.x;
            double y = pos.getY() - camera.y;
            double z = pos.getZ() - camera.z;
            LevelRenderer.renderLineBox(poseStack, vertices, x, y, z, x + 1, y + 1, z + 1,
                    r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
        }
        buffer.endBatch(WandRenderTypes.OUTLINES);
    }

    public static void renderPlacementPreview(PoseStack poseStack, Player player, Set<BlockPos> positions,
                                              Function<BlockPos, ItemStack> material, boolean replacing) {
        if (positions.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();
        VertexConsumer vertices = buffer.getBuffer(WandRenderTypes.GHOST_BLOCKS);
        RandomSource random = RandomSource.create();
        Level level = player.level();
        Inventory inventory = player.getInventory();
        Map<Integer, Integer> remainingItems = new HashMap<>();
        for (BlockPos pos : positions) {
            ItemStack requested = material.apply(pos);
            if (requested.isEmpty()) {
                continue;
            }
            ItemStack stack = requested;
            int slot = -1;
            if (!player.isCreative()) {
                slot = Tools.finditem(requested, inventory);
                if (slot < 0 || remainingItems.computeIfAbsent(slot, ignored -> Tools.countInventoryItems(requested, inventory)) <= 0) {
                    continue;
                }
                stack = inventory.getItem(slot);
            }

            BlockState state = Tools.getPlacementPreview(player, stack, pos, replacing);
            if (state == null) {
                continue;
            }
            if (slot >= 0) {
                remainingItems.put(slot, remainingItems.get(slot) - 1);
            }
            if (state.getRenderShape() != RenderShape.MODEL) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
            Vec3 modelOffset = state.getOffset(level, pos);
            poseStack.translate(modelOffset.x, modelOffset.y, modelOffset.z);
            renderBlockModel(minecraft, poseStack.last(), vertices, level, pos, state, random);
            poseStack.popPose();
        }
        buffer.endBatch(WandRenderTypes.GHOST_BLOCKS);
    }

    private static void renderBlockModel(Minecraft minecraft, PoseStack.Pose pose, VertexConsumer vertices,
                                         Level level, BlockPos pos, BlockState state, RandomSource random) {
        BakedModel model = minecraft.getBlockRenderer().getBlockModel(state);
        long seed = state.getSeed(pos);
        random.setSeed(seed);
        for (RenderType layer : model.getRenderTypes(state, random, ModelData.EMPTY)) {
            for (Direction side : DIRECTIONS) {
                random.setSeed(seed);
                renderQuads(minecraft, pose, vertices, level, pos, state, model.getQuads(state, side, random, ModelData.EMPTY, layer));
            }
            random.setSeed(seed);
            renderQuads(minecraft, pose, vertices, level, pos, state, model.getQuads(state, null, random, ModelData.EMPTY, layer));
        }
    }

    private static void renderQuads(Minecraft minecraft, PoseStack.Pose pose, VertexConsumer vertices,
                                    Level level, BlockPos pos, BlockState state, List<BakedQuad> quads) {
        for (BakedQuad quad : quads) {
            int color = quad.isTinted() ? minecraft.getBlockColors().getColor(state, level, pos, quad.getTintIndex()) : 0xFFFFFF;
            float shade = level.getShade(quad.getDirection(), quad.isShade());
            float red = ((color >> 16) & 0xFF) / 255.0f * shade;
            float green = ((color >> 8) & 0xFF) / 255.0f * shade;
            float blue = (color & 0xFF) / 255.0f * shade;
            vertices.putBulkData(pose, quad, QUAD_BRIGHTNESS, red, green, blue, PREVIEW_ALPHA, QUAD_LIGHT, OverlayTexture.NO_OVERLAY, true);
        }
    }
}
