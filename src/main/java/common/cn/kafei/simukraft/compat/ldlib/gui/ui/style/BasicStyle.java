package common.cn.kafei.simukraft.compat.ldlib.gui.ui.style;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;

public final class BasicStyle {
    private final UIElement element;

    public BasicStyle(UIElement element) {
        this.element = element;
    }

    public BasicStyle backgroundTexture(IGuiTexture texture) {
        element.setBackground(texture == null ? IGuiTexture.EMPTY : texture);
        return this;
    }

    public BasicStyle zIndex(int value) {
        element.setZIndex(value);
        return this;
    }
}
