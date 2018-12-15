package romelo333.notenoughwands.mcjtylib;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextFormat;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class RenderHelper {
    public static float rot = 0.0f;

    public static void renderEntity(Entity entity, int xPos, int yPos) {
        float f1 = 10F;
        renderEntity(entity, xPos, yPos, f1);
    }

    public static void renderEntity(Entity entity, int xPos, int yPos, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.color3f(1f, 1f, 1f);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(xPos + 8, yPos + 16, 50F);
        GlStateManager.scalef(-scale, scale, scale);
        GlStateManager.rotatef(180F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotatef(135F, 0.0F, 1.0F, 0.0F);
        // @todo fabric
//        net.minecraft.client.render.Renderer.enableStandardItemLighting();
        GlStateManager.rotatef(-135F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(rot, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
        entity.pitch = 0.0F;
        GlStateManager.translatef(0.0F, (float) entity.getHeightOffset(), 0.0F);
        MinecraftClient.getInstance().getEntityRenderManager().field_4679 = 180F;   // playerViewY @todo fabric
        MinecraftClient.getInstance().getEntityRenderManager().method_3954(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);    // render @todo fabric
        GlStateManager.popMatrix();
        // @todo fabric
//        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.translatef(0F, 0F, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        int i1 = 240;
        int k1 = 240;
        // @todo fabric
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1 / 1.0F, k1 / 1.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableRescaleNormal();
        // @todo fabric
//        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.popMatrix();
    }

    public static boolean renderObject(MinecraftClient mc, int x, int y, Object itm, boolean highlight) {
        if (itm instanceof Entity) {
            renderEntity((Entity) itm, x, y);
            return true;
        }
        ItemRenderer itemRender = MinecraftClient.getInstance().getItemRenderer();
        return renderObject(mc, itemRender, x, y, itm, highlight, 200);
    }

    public static boolean renderObject(MinecraftClient mc, ItemRenderer itemRender, int x, int y, Object itm, boolean highlight, float lvl) {
        itemRender.zOffset = lvl;

        if (itm==null) {
            return renderItemStack(mc, itemRender, ItemStack.EMPTY, x, y, "", highlight);
        }
        if (itm instanceof Item) {
            return renderItemStack(mc, itemRender, new ItemStack((Item) itm, 1), x, y, "", highlight);
        }
        if (itm instanceof Block) {
            return renderItemStack(mc, itemRender, new ItemStack((Block) itm, 1), x, y, "", highlight);
        }
        if (itm instanceof ItemStack) {
            return renderItemStackWithCount(mc, itemRender, (ItemStack) itm, x, y, highlight);
        }
        // @todo fabric
//        if (itm instanceof FluidStack) {
//            return renderFluidStack(mc, (FluidStack) itm, x, y, highlight);
//        }
        if (itm instanceof Sprite) {
            return renderIcon(mc, itemRender, (Sprite) itm, x, y, highlight);
        }
        return renderItemStack(mc, itemRender, ItemStack.EMPTY, x, y, "", highlight);
    }

    public static boolean renderIcon(MinecraftClient mc, ItemRenderer itemRender, Sprite itm, int xo, int yo, boolean highlight) {
        //itemRender.draw(xo, yo, itm, 16, 16); //TODO: Make
        return true;
    }

    // @todo fabric
    //    public static boolean renderFluidStack(MinecraftClient mc, FluidStack fluidStack, int x, int y, boolean highlight) {
//        Fluid fluid = fluidStack.getFluid();
//        if (fluid == null) {
//            return false;
//        }
//
//        TextureMap textureMapBlocks = mc.getTextureMapBlocks();
//        ResourceLocation fluidStill = fluid.getStill();
//        TextureAtlasSprite fluidStillSprite = null;
//        if (fluidStill != null) {
//            fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
//        }
//        if (fluidStillSprite == null) {
//            fluidStillSprite = textureMapBlocks.getMissingSprite();
//        }
//
//        int fluidColor = fluid.getColor(fluidStack);
//        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//        setGLColorFromInt(fluidColor);
//        drawFluidTexture(x, y, fluidStillSprite, 100);
//
//        return true;
//    }

    private static void drawFluidTexture(double xCoord, double yCoord, Sprite textureSprite, double zLevel) {
        double uMin = textureSprite.getMinU();
        double uMax = textureSprite.getMaxU();
        double vMin = textureSprite.getMinV();
        double vMax = textureSprite.getMaxV();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
        vertexBuffer.begin(7, VertexFormats.POSITION_UV);
        vertexBuffer.vertex(xCoord, yCoord + 16, zLevel).texture(uMin, vMax).next();
        vertexBuffer.vertex(xCoord + 16, yCoord + 16, zLevel).texture(uMax, vMax).next();
        vertexBuffer.vertex(xCoord + 16, yCoord, zLevel).texture(uMax, vMin).next();
        vertexBuffer.vertex(xCoord, yCoord, zLevel).texture(uMin, vMin).next();
        tessellator.draw();
    }


    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.color4f(red, green, blue, 1.0F);
    }


    public static boolean renderItemStackWithCount(MinecraftClient mc, ItemRenderer itemRender, ItemStack itm, int xo, int yo, boolean highlight) {
        int size = itm.getAmount();
        String amount;
        if (size <= 1) {
            amount = "";
        } else if (size < 100000) {
            amount = String.valueOf(size);
        } else if (size < 1000000) {
            amount = String.valueOf(size / 1000) + "k";
        } else if (size < 1000000000) {
            amount = String.valueOf(size / 1000000) + "m";
        } else {
            amount = String.valueOf(size / 1000000000) + "g";
        }

        return renderItemStack(mc, itemRender, itm, xo, yo, amount, highlight);
//        if (itm.stackSize==1 || itm.stackSize==0) {
//            return renderItemStack(mc, itemRender, itm, xo, yo, "", highlight);
//        } else {
//            return renderItemStack(mc, itemRender, itm, xo, yo, "" + itm.stackSize, highlight);
//        }
    }

    public static boolean renderItemStack(MinecraftClient mc, ItemRenderer itemRender, ItemStack itm, int x, int y, String txt, boolean highlight){
        GlStateManager.color3f(1F, 1F, 1F);

        boolean rc = false;
        if (highlight){
            GlStateManager.disableLighting();
            drawVerticalGradientRect(x, y, x+16, y+16, 0x80ffffff, 0xffffffff);
        }
        if (!itm.isEmpty() && itm.getItem() != null) {
            rc = true;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 0.0F, 32.0F);
            GlStateManager.color4f(1F, 1F, 1F, 1F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableLighting();
            short short1 = 240;
            short short2 = 240;
            // @todo fabric
//            net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
            itemRender.renderItemAndGlowInGui(itm, x, y);
            renderItemOverlayIntoGUI(mc.fontRenderer, itm, x, y, txt, txt.length() - 2);
//            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itm, x, y, txt);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
        }

        return rc;
    }


    /**
     * Renders the stack size and/or damage bar for the given ItemStack.
     */
    private static void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text,
                                                 int scaled) {
        if (!stack.isEmpty()) {
            int stackSize = stack.getAmount();
            if (stackSize != 1 || text != null) {
                String s = text == null ? String.valueOf(stackSize) : text;
                if (text == null && stackSize < 1) {
                    s = TextFormat.RED + String.valueOf(stackSize);
                }

                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                GlStateManager.disableBlend();
                if (scaled >= 2) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(.5f, .5f, .5f);
                    fr.drawWithShadow(s, ((xPosition + 19 - 2) * 2 - 1 - fr.getStringWidth(s)), yPosition * 2 + 24, 16777215);
                    GlStateManager.popMatrix();
                } else if (scaled == 1) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scalef(.75f, .75f, .75f);
                    fr.drawWithShadow(s, ((xPosition - 2) * 1.34f + 24 - fr.getStringWidth(s)), yPosition * 1.34f + 14, 16777215);
                    GlStateManager.popMatrix();
                } else {
                    fr.drawWithShadow(s, (xPosition + 19 - 2 - fr.getStringWidth(s)), (yPosition + 6 + 3), 16777215);
                }
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
                // Fixes opaque cooldown overlay a bit lower
                // TODO: check if enabled blending still screws things up down the line.
                GlStateManager.enableBlend();
            }

            // @todo fabric
//            if (stack.getItem().showDurabilityBar(stack)) {
//                double health = stack.getItem().getDurabilityForDisplay(stack);
//                int j = (int) Math.round(13.0D - health * 13.0D);
//                int i = (int) Math.round(255.0D - health * 255.0D);
//                GlStateManager.disableLighting();
//                GlStateManager.disableDepth();
//                GlStateManager.disableTexture2D();
//                GlStateManager.disableAlpha();
//                GlStateManager.disableBlend();
//                Tessellator tessellator = Tessellator.getInstance();
//                BufferBuilder vertexbuffer = tessellator.getBuffer();
//                draw(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
//                draw(vertexbuffer, xPosition + 2, yPosition + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
//                draw(vertexbuffer, xPosition + 2, yPosition + 13, j, 1, 255 - i, i, 0, 255);
//                GlStateManager.enableBlend();
//                GlStateManager.enableAlpha();
//                GlStateManager.enableTexture2D();
//                GlStateManager.enableLighting();
//                GlStateManager.enableDepth();
//            }

            ClientPlayerEntity entityplayersp = MinecraftClient.getInstance().player;
            float f = entityplayersp == null ? 0.0F : entityplayersp.getItemCooldownManager().method_7905(stack.getItem(), MinecraftClient.getInstance().getTickDelta());

            if (f > 0.0F) {
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                GlStateManager.disableTexture();
                Tessellator tessellator1 = Tessellator.getInstance();
                VertexBuffer vertexbuffer1 = tessellator1.getVertexBuffer();
                draw(vertexbuffer1, xPosition, yPosition + MathTools.floor(16.0F * (1.0F - f)), 16, MathTools.ceiling(16.0F * f), 255, 255, 255, 127);
                GlStateManager.enableTexture();
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
            }
        }
    }

    /**
     * Draw with the WorldRenderer
     */
    private static void draw(VertexBuffer renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        renderer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        renderer.vertex((x + 0), (y + 0), 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex((x + 0), (y + height), 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex((x + width), (y + height), 0.0D).color(red, green, blue, alpha).next();
        renderer.vertex((x + width), (y + 0), 0.0D).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();
    }


    /**
     * Draws a rectangle with a vertical gradient between the specified colors.
     * x2 and y2 are not included.
     */
    public static void drawVerticalGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
//        this.zLevel = 300.0F;
        float zLevel = 0.0f;

        float f = (color1 >> 24 & 255) / 255.0F;
        float f1 = (color1 >> 16 & 255) / 255.0F;
        float f2 = (color1 >> 8 & 255) / 255.0F;
        float f3 = (color1 & 255) / 255.0F;
        float f4 = (color2 >> 24 & 255) / 255.0F;
        float f5 = (color2 >> 16 & 255) / 255.0F;
        float f6 = (color2 >> 8 & 255) / 255.0F;
        float f7 = (color2 & 255) / 255.0F;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
    // @todo fabric
        //        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);

        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getVertexBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x2, y1, zLevel).color(f1, f2, f3, f).next();
        buffer.vertex(x1, y1, zLevel).color(f1, f2, f3, f).next();
        buffer.vertex(x1, y2, zLevel).color(f5, f6, f7, f4).next();
        buffer.vertex(x2, y2, zLevel).color(f5, f6, f7, f4).next();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    /**
     * Draws a rectangle with a horizontal gradient between the specified colors.
     * x2 and y2 are not included.
     */
    public static void drawHorizontalGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
//        this.zLevel = 300.0F;
        float zLevel = 0.0f;

        float f = (color1 >> 24 & 255) / 255.0F;
        float f1 = (color1 >> 16 & 255) / 255.0F;
        float f2 = (color1 >> 8 & 255) / 255.0F;
        float f3 = (color1 & 255) / 255.0F;
        float f4 = (color2 >> 24 & 255) / 255.0F;
        float f5 = (color2 >> 16 & 255) / 255.0F;
        float f6 = (color2 >> 8 & 255) / 255.0F;
        float f7 = (color2 & 255) / 255.0F;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getVertexBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y1, zLevel).color(f1, f2, f3, f).next();
        buffer.vertex(x1, y2, zLevel).color(f1, f2, f3, f).next();
        buffer.vertex(x2, y2, zLevel).color(f5, f6, f7, f4).next();
        buffer.vertex(x2, y1, zLevel).color(f5, f6, f7, f4).next();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    public static void drawHorizontalLine(int x1, int y1, int x2, int color) {
        Gui.drawRect(x1, y1, x2, y1+1, color);
    }

    public static void drawVerticalLine(int x1, int y1, int y2, int color) {
        Gui.drawRect(x1, y1, x1+1, y2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the left point
    public static void drawLeftTriangle(int x, int y, int color) {
        drawVerticalLine(x, y, y, color);
        drawVerticalLine(x + 1, y - 1, y + 1, color);
        drawVerticalLine(x + 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the right point
    public static void drawRightTriangle(int x, int y, int color) {
        drawVerticalLine(x, y, y, color);
        drawVerticalLine(x - 1, y - 1, y + 1, color);
        drawVerticalLine(x - 2, y - 2, y + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the top point
    public static void drawUpTriangle(int x, int y, int color) {
        drawHorizontalLine(x, y, x, color);
        drawHorizontalLine(x-1, y+1, x+1, color);
        drawHorizontalLine(x - 2, y + 2, x + 2, color);
    }

    // Draw a small triangle. x,y is the coordinate of the bottom point
    public static void drawDownTriangle(int x, int y, int color) {
        drawHorizontalLine(x, y, x, color);
        drawHorizontalLine(x-1, y-1, x+1, color);
        drawHorizontalLine(x-2, y-2, x+2, color);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThickButtonBox(int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        Gui.drawRect(x1+2, y1+2, x2-2, y2-2, average);
        drawHorizontalLine(x1+1, y1, x2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(x1+1, y2-1, x2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(x1, y1 + 1, y2 - 1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(x2-1, y1+1, y2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(x1+1, y1+1, x2-1, bright);
        drawHorizontalLine(x1+2, y1+2, x2-2, bright);
        drawVerticalLine(x1+1, y1+2, y2-2, bright);
        drawVerticalLine(x1+2, y1+3, y2-3, bright);

        drawHorizontalLine(x1+3, y2-3, x2-2, dark);
        drawHorizontalLine(x1+2, y2-2, x2-1, dark);
        drawVerticalLine(x2 - 2, y1 + 2, y2 - 2, dark);
        drawVerticalLine(x2 - 3, y1 + 3, y2 - 3, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThinButtonBox(int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        Gui.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average);
        drawHorizontalLine(x1+1, y1, x2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(x1+1, y2-1, x2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(x1, y1 + 1, y2 - 1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(x2-1, y1+1, y2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(x1+1, y1+1, x2-2, bright);
        drawVerticalLine(x1 + 1, y1 + 2, y2 - 3, bright);

        drawHorizontalLine(x1 + 1, y2 - 2, x2 - 1, dark);
        drawVerticalLine(x2-2, y1+1, y2-2, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawThinButtonBoxGradient(int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
        drawHorizontalLine(x1+1, y1, x2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawHorizontalLine(x1+1, y2-1, x2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(x1, y1 + 1, y2 - 1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);
        drawVerticalLine(x2-1, y1+1, y2-1, 0xff000000); // @todo fabric: StyleConfig.colorButtonExternalBorder);

        drawHorizontalLine(x1+1, y1+1, x2-2, bright);
        drawVerticalLine(x1 + 1, y1 + 2, y2 - 3, bright);

        drawHorizontalLine(x1 + 1, y2 - 2, x2 - 1, dark);
        drawVerticalLine(x2-2, y1+1, y2-2, dark);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBox(int x1, int y1, int x2, int y2, int bright, int average, int dark) {
        drawBeveledBox(x1, y1, x2, y2, bright, dark, average);
    }

    /**
     * Draw a button box. x2 and y2 are not included.
     */
    public static void drawFlatButtonBoxGradient(int x1, int y1, int x2, int y2, int bright, int average1, int average2, int dark) {
        drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, average2, average1);
        drawHorizontalLine(x1, y1, x2-1, bright);
        drawVerticalLine(x1, y1, y2-1, bright);
        drawVerticalLine(x2-1, y1, y2-1, dark);
        drawHorizontalLine(x1, y2 - 1, x2, dark);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawBeveledBox(int x1, int y1, int x2, int y2, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Gui.drawRect(x1+1, y1+1, x2-1, y2-1, fillcolor);
        }
        drawHorizontalLine(x1, y1, x2-1, topleftcolor);
        drawVerticalLine(x1, y1, y2-1, topleftcolor);
        drawVerticalLine(x2-1, y1, y2-1, botrightcolor);
        drawHorizontalLine(x1, y2-1, x2, botrightcolor);
    }

    /**
     * Draw a thick beveled box. x2 and y2 are not included.
     */
    public static void drawThickBeveledBox(int x1, int y1, int x2, int y2, int thickness, int topleftcolor, int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Gui.drawRect(x1+1, y1+1, x2-1, y2-1, fillcolor);
        }
        Gui.drawRect(x1, y1, x2-1, y1+thickness, topleftcolor);
        Gui.drawRect(x1, y1, x1+thickness, y2-1, topleftcolor);
        Gui.drawRect(x2-thickness, y1, x2, y2-1, botrightcolor);
        Gui.drawRect(x1, y2 - thickness, x2, y2, botrightcolor);
    }

    /**
     * Draw a beveled box. x2 and y2 are not included.
     */
    public static void drawFlatBox(int x1, int y1, int x2, int y2, int border, int fill) {
        if (fill != -1) {
            Gui.drawRect(x1+1, y1+1, x2-1, y2-1, fill);
        }
        drawHorizontalLine(x1, y1, x2-1, border);
        drawVerticalLine(x1, y1, y2-1, border);
        drawVerticalLine(x2-1, y1, y2-1, border);
        drawHorizontalLine(x1, y2-1, x2, border);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        float zLevel = 0.01f;
        float f = (1/256.0f);
        float f1 = (1/256.0f);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getVertexBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
        buffer.vertex((x + 0), (y + height), zLevel).texture(((u + 0.0) * f), ((v + height) * f1)).next();
        buffer.vertex((x + width), (y + height), zLevel).texture(((u + width) * f), ((v + height) * f1)).next();
        buffer.vertex((x + width), (y + 0), zLevel).texture(((u + width) * f), ((v + 0.0) * f1)).next();
        buffer.vertex((x + 0), (y + 0), zLevel).texture(((u + 0.0) * f), ((v + 0.0) * f1)).next();
        tessellator.draw();
    }

    public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int totw, int toth) {
        float f = 1.0f/totw;
        float f1 = 1.0f/toth;
        double zLevel = 50;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getVertexBuffer();
        vertexbuffer.begin(7, VertexFormats.POSITION_UV);
        vertexbuffer.vertex((x + 0), (y + height), zLevel).texture(((textureX + 0.0) *  f), ((textureY + height) * f1)).next();
        vertexbuffer.vertex((x + width), (y + height), zLevel).texture(((textureX + width) * f), ((textureY + height) * f1)).next();
        vertexbuffer.vertex((x + width), (y + 0), zLevel).texture(((textureX + width) * f), ((textureY + 0.0) * f1)).next();
        vertexbuffer.vertex((x + 0), (y + 0), zLevel).texture(((textureX + 0.0) * f), ((textureY + 0.0) * f1)).next();
        tessellator.draw();
    }

    public static void renderBillboardQuadBright(double scale) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;
        GlStateManager.pushMatrix();
        RenderHelper.rotateToPlayer();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getVertexBuffer();
        buffer.begin(7, VertexFormats.field_1586); // @todo fabric: POSITION_TEX_LMAP_COLOR);
        buffer.vertex(-scale, -scale, 0.0D).texture(0.0D, 0.0D).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(-scale, scale, 0.0D).texture(0.0D, 1.0D).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(scale, scale, 0.0D).texture(1.0D, 1.0D).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(scale, -scale, 0.0D).texture(1.0D, 0.0D).texture(b1, b2).color(255, 255, 255, 128).next();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static void renderBillboardQuad(double scale) {
        GlStateManager.pushMatrix();

        rotateToPlayer();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getVertexBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
        buffer.vertex(-scale, -scale, 0).texture(0.0, 0.0).next();
        buffer.vertex(-scale, +scale, 0).texture(0.0, 1.0).next();
        buffer.vertex(+scale, +scale, 0).texture(1.0, 1.0).next();
        buffer.vertex(+scale, -scale, 0).texture(1.0, 0.0).next();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static void renderBillboardQuadWithRotation(float rot, double scale) {
        GlStateManager.pushMatrix();

        rotateToPlayer();

        GlStateManager.rotatef(rot, 0, 0, 1);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getVertexBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
        buffer.vertex(-scale, -scale, 0).texture(0.0, 0.0).next();
        buffer.vertex(-scale, +scale, 0).texture(0.0, 1.0).next();
        buffer.vertex(+scale, +scale, 0).texture(1.0, 1.0).next();
        buffer.vertex(+scale, -scale, 0).texture(1.0, 0.0).next();
        tessellator.draw();
        GlStateManager.popMatrix();
    }

    public static void rotateToPlayer() {
        GlStateManager.rotatef(-MinecraftClient.getInstance().getEntityRenderManager().field_4679, 0.0F, 1.0F, 0.0F);   // @todo fabric playerViewY
        GlStateManager.rotatef(MinecraftClient.getInstance().getEntityRenderManager().field_4677, 1.0F, 0.0F, 0.0F);   // @todo fabric playerViewX
    }

    public static int renderText(MinecraftClient mc, int x, int y, String txt) {
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0F, 0.0F, 32.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableLighting();
        // @todo fabric
//        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.disableBlend();
        int width = mc.fontRenderer.getStringWidth(txt);
        mc.fontRenderer.drawWithShadow(txt, x, y, 16777215);
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager.enableBlend();


        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();

        return width;
    }

    public static int renderText(MinecraftClient mc, int x, int y, String txt, int color) {
        GlStateManager.color3f(1.0F, 1.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0F, 0.0F, 32.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableLighting();
        // @todo fabric
//        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        GlStateManager.disableBlend();
        int width = mc.fontRenderer.getStringWidth(txt);
        mc.fontRenderer.draw(txt, x, y, color);
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        // Fixes opaque cooldown overlay a bit lower
        // TODO: check if enabled blending still screws things up down the line.
        GlStateManager.enableBlend();


        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();

        return width;
    }


    /**
     * Draw a beam with some thickness.
     * @param S
     * @param E
     * @param P
     * @param width
     */
    public static void drawBeam(Vector S, Vector E, Vector P, float width) {
        Vector PS = Sub(S, P);
        Vector SE = Sub(E, S);

        Vector normal = Cross(PS, SE);
        normal = normal.normalize();

        Vector half = Mul(normal, width);
        Vector p1 = Add(S, half);
        Vector p2 = Sub(S, half);
        Vector p3 = Add(E, half);
        Vector p4 = Sub(E, half);

        drawQuad(Tessellator.getInstance(), p1, p3, p4, p2);
    }

    private static void drawQuad(Tessellator tessellator, Vector p1, Vector p2, Vector p3, Vector p4) {
        int brightness = 240;
        int b1 = brightness >> 16 & 65535;
        int b2 = brightness & 65535;

        VertexBuffer buffer = tessellator.getVertexBuffer();
        buffer.vertex(p1.getX(), p1.getY(), p1.getZ()).texture(0.0D, 0.0D).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(p2.getX(), p2.getY(), p2.getZ()).texture(1.0D, 0.0D).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(p3.getX(), p3.getY(), p3.getZ()).texture(1.0D, 1.0D).texture(b1, b2).color(255, 255, 255, 128).next();
        buffer.vertex(p4.getX(), p4.getY(), p4.getZ()).texture(0.0D, 1.0D).texture(b1, b2).color(255, 255, 255, 128).next();
    }

    public static class Vector {
        public final float x;
        public final float y;
        public final float z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        public float norm() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        public Vector normalize() {
            float n = norm();
            return new Vector(x / n, y / n, z / n);
        }
    }

    private static Vector Cross(Vector a, Vector b) {
        float x = a.y*b.z - a.z*b.y;
        float y = a.z*b.x - a.x*b.z;
        float z = a.x*b.y - a.y*b.x;
        return new Vector(x, y, z);
    }

    private static Vector Sub(Vector a, Vector b) {
        return new Vector(a.x-b.x, a.y-b.y, a.z-b.z);
    }
    private static Vector Add(Vector a, Vector b) {
        return new Vector(a.x+b.x, a.y+b.y, a.z+b.z);
    }
    private static Vector Mul(Vector a, float f) {
        return new Vector(a.x * f, a.y * f, a.z * f);
    }

    public static void renderHighLightedBlocksOutline(VertexBuffer buffer, float mx, float my, float mz, float r, float g, float b, float a) {
        buffer.vertex(mx, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz).color(r, g, b, a).next();

        buffer.vertex(mx, my+1, mz).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz).color(r, g, b, a).next();

        buffer.vertex(mx+1, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz).color(r, g, b, a).next();
        buffer.vertex(mx+1, my+1, mz).color(r, g, b, a).next();

        buffer.vertex(mx, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx+1, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my, mz+1).color(r, g, b, a).next();
        buffer.vertex(mx, my+1, mz+1).color(r, g, b, a).next();
    }


}
