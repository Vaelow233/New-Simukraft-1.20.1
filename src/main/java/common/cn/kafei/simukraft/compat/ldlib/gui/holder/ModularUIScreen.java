package common.cn.kafei.simukraft.compat.ldlib.gui.holder;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.ModularUI;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Client-only host for LowDragLib 1 widgets that do not need a container or a logged-in player. */
public class ModularUIScreen extends Screen {
    private final ModularUI modularUI;
    private final UIElement root;

    public ModularUIScreen(ModularUI modularUI, Component title) {
        super(title == null ? Component.empty() : title);
        this.modularUI = modularUI;
        this.root = modularUI.root();
    }

    public Component getScreenTitle() {
        return title;
    }

    @Override
    protected void init() {
        modularUI.prepare(width, height);
        root.setClientSideWidget();
        root.initWidget();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        root.drawInBackground(graphics, mouseX, mouseY, partialTick);
        root.drawInForeground(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        root.updateScreen();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return root.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return root.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return root.mouseDragged(mouseX, mouseY, button, dragX, dragY)
                || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return root.mouseWheelMove(mouseX, mouseY, delta) || super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modularUI.shouldCloseOnInventoryKey()
                && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            return false;
        }
        return root.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return root.keyReleased(keyCode, scanCode, modifiers) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return root.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return modularUI.shouldCloseOnEsc();
    }

    @Override
    public void removed() {
        root.dispose();
        super.removed();
    }
}
