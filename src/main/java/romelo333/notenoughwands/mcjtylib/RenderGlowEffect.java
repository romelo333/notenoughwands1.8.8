package romelo333.notenoughwands.mcjtylib;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderGlowEffect {

    /**
     * Render a glow effect at the given position. The texture to use
     * for glowing should be bound before calling this.
     */
    public static void renderGlow(Tessellator tessellator, double x, double y, double z) {
        BufferBuilder buffer = tessellator.getBufferBuilder();
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        buffer.begin(GL11.GL_QUADS, VertexFormats.field_1586);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderGlowEffect.addSideFullTexture(buffer, Direction.UP.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(buffer, Direction.DOWN.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(buffer, Direction.NORTH.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(buffer, Direction.SOUTH.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(buffer, Direction.WEST.ordinal(), 1.1f, -0.05f);
        RenderGlowEffect.addSideFullTexture(buffer, Direction.EAST.ordinal(), 1.1f, -0.05f);

        tessellator.draw();
        GlStateManager.popMatrix();
    }


    private static final Quad[] quads = new Quad[] {
            new Quad(new Vt(0, 0, 0), new Vt(1, 0, 0), new Vt(1, 0, 1), new Vt(0, 0, 1)),       // DOWN
            new Quad(new Vt(0, 1, 1), new Vt(1, 1, 1), new Vt(1, 1, 0), new Vt(0, 1, 0)),       // UP
            new Quad(new Vt(1, 1, 0), new Vt(1, 0, 0), new Vt(0, 0, 0), new Vt(0, 1, 0)),       // NORTH
            new Quad(new Vt(1, 0, 1), new Vt(1, 1, 1), new Vt(0, 1, 1), new Vt(0, 0, 1)),       // SOUTH
            new Quad(new Vt(0, 0, 1), new Vt(0, 1, 1), new Vt(0, 1, 0), new Vt(0, 0, 0)),       // WEST
            new Quad(new Vt(1, 0, 0), new Vt(1, 1, 0), new Vt(1, 1, 1), new Vt(1, 0, 1)),       // EAST
    };

    public static void addSideFullTexture(BufferBuilder buffer, int side, float mult, float offset, Vec3d offs) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
        Quad quad = quads[side];
        buffer.vertex(offs.x + quad.v1.x * mult + offset, offs.y + quad.v1.y * mult + offset, offs.z + quad.v1.z * mult + offset).texture(0.0, 0.0).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(offs.x + quad.v2.x * mult + offset, offs.y + quad.v2.y * mult + offset, offs.z + quad.v2.z * mult + offset).texture(0.0, 1.0).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(offs.x + quad.v3.x * mult + offset, offs.y + quad.v3.y * mult + offset, offs.z + quad.v3.z * mult + offset).texture(1.0, 1.0).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(offs.x + quad.v4.x * mult + offset, offs.y + quad.v4.y * mult + offset, offs.z + quad.v4.z * mult + offset).texture(1.0, 0.0).texture(b1, b2).color(255, 255, 255, 128).next();
    }

    public static void addSideFullTexture(BufferBuilder buffer, int side, float mult, float offset) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
        Quad quad = quads[side];
        buffer.vertex(quad.v1.x * mult + offset, quad.v1.y * mult + offset, quad.v1.z * mult + offset).texture(0.0, 0.0).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(quad.v2.x * mult + offset, quad.v2.y * mult + offset, quad.v2.z * mult + offset).texture(0.0, 1.0).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(quad.v3.x * mult + offset, quad.v3.y * mult + offset, quad.v3.z * mult + offset).texture(1.0, 1.0).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(quad.v4.x * mult + offset, quad.v4.y * mult + offset, quad.v4.z * mult + offset).texture(1.0, 0.0).texture(b1, b2).color(255, 255, 255, 128).next();
    }

    private static class Vt {
        public final float x;
        public final float y;
        public final float z;

        public Vt(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static class Quad {
        public final Vt v1;
        public final Vt v2;
        public final Vt v3;
        public final Vt v4;

        public Quad(Vt v1, Vt v2, Vt v3, Vt v4) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
            this.v4 = v4;
        }

        public Quad rotate(Direction direction) {
            switch (direction) {
                case NORTH: return new Quad(v4, v1, v2, v3);
                case EAST: return new Quad(v3, v4, v1, v2);
                case SOUTH: return new Quad(v2, v3, v4, v1);
                case WEST: return this;
                default: return this;
            }
        }
    }
}
