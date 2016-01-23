package romelo333.notenoughwands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public class FreezePotion extends Potion {
    public static FreezePotion freezePotion;

    ResourceLocation icon = new ResourceLocation(NotEnoughWands.MODID + ":textures/gui/effects/freeze.png");

    public FreezePotion() {
        super(new ResourceLocation("freeze"), false, Color.BLUE.getRed());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        super.renderInventoryEffect(x, y, effect, mc);

        mc.renderEngine.bindTexture(icon);

        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);

    }
}
