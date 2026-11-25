package romelo333.notenoughwands.varia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class ClientTools {

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int PREVIEW_ALPHA = 77;

    private ClientTools() {
    }

    public record RenderContext(PoseStack poses, SubmitNodeCollector collector, Vec3 camera) {
    }

    public static void renderOutlines(RenderContext context, Set<BlockPos> positions, int r, int g, int b) {
        if (positions.isEmpty()) {
            return;
        }
        List<BlockPos> targets = List.copyOf(positions);
        Vec3 camera = context.camera();
        int color = ARGB.color(255, r, g, b);
        context.collector().submitCustomGeometry(context.poses(), WandRenderTypes.OUTLINES, (pose, vertices) -> {
            for (BlockPos pos : targets) {
                double x = pos.getX() - camera.x;
                double y = pos.getY() - camera.y;
                double z = pos.getZ() - camera.z;
                Shapes.block().forAllEdges((x1, y1, z1, x2, y2, z2) -> {
                    Vector3f normal = new Vector3f((float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1)).normalize();
                    vertices.addVertex(pose, (float) (x + x1), (float) (y + y1), (float) (z + z1))
                            .setColor(color).setNormal(pose, normal).setLineWidth(4.0f);
                    vertices.addVertex(pose, (float) (x + x2), (float) (y + y2), (float) (z + z2))
                            .setColor(color).setNormal(pose, normal).setLineWidth(4.0f);
                });
            }
        });
    }

    public static void renderPlacementPreview(RenderContext context, Player player, Set<BlockPos> positions,
                                              Function<BlockPos, ItemStack> material, boolean replacing) {
        if (positions.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Vec3 camera = context.camera();
        PoseStack poseStack = context.poses();
        RandomSource random = RandomSource.create();
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
            Vec3 modelOffset = state.getOffset(pos);
            poseStack.translate(modelOffset.x, modelOffset.y, modelOffset.z);
            var parts = getModelParts(minecraft, state, pos, random);
            context.collector().submitCustomGeometry(poseStack, WandRenderTypes.GHOST_BLOCKS,
                    (pose, vertices) -> renderBlockModel(minecraft, pose, vertices, pos, state, parts, replacing));
            poseStack.popPose();
        }
    }

    private static List<BlockStateModelPart> getModelParts(Minecraft minecraft, BlockState state, BlockPos pos, RandomSource random) {
        BlockStateModel model = minecraft.getModelManager().getBlockStateModelSet().get(state);
        List<BlockStateModelPart> parts = new ArrayList<>();
        random.setSeed(state.getSeed(pos));
        model.collectParts(minecraft.level, pos, state, random, parts);
        return parts;
    }

    private static void renderBlockModel(Minecraft minecraft, PoseStack.Pose pose, VertexConsumer vertices,
                                         BlockPos pos, BlockState state, List<BlockStateModelPart> parts, boolean replacing) {
        for (BlockStateModelPart part : parts) {
            for (Direction side : DIRECTIONS) {
                renderQuads(minecraft, pose, vertices, pos, state, part.getQuads(side), replacing);
            }
            renderQuads(minecraft, pose, vertices, pos, state, part.getQuads(null), replacing);
        }
    }

    private static void renderQuads(Minecraft minecraft, PoseStack.Pose pose, VertexConsumer vertices,
                                    BlockPos pos, BlockState state, List<BakedQuad> quads, boolean replacing) {
        for (BakedQuad quad : quads) {
            var material = quad.materialInfo();
            var tintSource = material.isTinted() ? minecraft.getBlockColors().getTintSource(state, material.tintIndex()) : null;
            int tint = tintSource == null ? 0xFFFFFFFF : tintSource.colorInWorld(state, minecraft.level, pos);
            Direction side = quad.direction();
            Direction shadedSide = material.shadeDirectionOverride() == null ? side : material.shadeDirectionOverride();
            float shade = switch (shadedSide) {
                case UP -> 1.0f;
                case DOWN -> 0.55f;
                case NORTH, SOUTH -> 0.8f;
                case EAST, WEST -> 0.65f;
            };
            // Move coplanar replacement faces just ahead of the old block.
            float offset = replacing ? 0.002f : 0.0f;
            for (int vertex = 0; vertex < 4; vertex++) {
                var point = quad.position(vertex);
                long uv = quad.packedUV(vertex);
                int color = ARGB.multiply(tint, quad.bakedColors().color(vertex));
                vertices.addVertex(pose, point.x() + side.getStepX() * offset, point.y() + side.getStepY() * offset, point.z() + side.getStepZ() * offset)
                        .setUv(UVPair.unpackU(uv), UVPair.unpackV(uv))
                        .setColor((int) (ARGB.red(color) * shade), (int) (ARGB.green(color) * shade),
                                (int) (ARGB.blue(color) * shade), PREVIEW_ALPHA);
            }
        }
    }
}
