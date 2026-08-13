package common.cn.kafei.simukraft.compat.ldlib.gui.ui.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class GUIContext {
    public final Minecraft mc;
    public final GuiGraphics graphics;
    public final PoseStack pose;
    public final int mouseX;
    public final int mouseY;
    public final float partialTick;

    public GUIContext(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.mc = Minecraft.getInstance();
        this.graphics = graphics;
        this.pose = graphics.pose();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTick = partialTick;
    }

    public void enableScissor(int left, int top, int width, int height) {
        graphics.enableScissor(left, top, left + width, top + height);
    }

    public void disableScissor() {
        graphics.disableScissor();
    }
}
