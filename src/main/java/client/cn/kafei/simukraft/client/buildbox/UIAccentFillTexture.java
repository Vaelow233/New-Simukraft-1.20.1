package client.cn.kafei.simukraft.client.buildbox;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;

@SuppressWarnings("null")
@OnlyIn(Dist.CLIENT)
final class UIAccentFillTexture implements IGuiTexture {
    private final int color;

    UIAccentFillTexture(int color) {
        this.color = color;
    }

    @Override
    public void draw(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float x, float y, int width, int height) {
        float drawX = x + 2;
        float drawY = y + 2;
        int drawWidth = Math.max(0, Math.round(width * 0.55F - 4));
        int drawHeight = Math.max(0, height - 4);
        if (drawWidth <= 0 || drawHeight <= 0) {
            return;
        }
        new GuiTextureGroup(new ColorRectTexture(color)).draw(graphics, mouseX, mouseY, drawX, drawY, drawWidth, drawHeight);
    }
}
