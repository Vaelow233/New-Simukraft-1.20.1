package common.cn.kafei.simukraft.compat.ldlib.gui.editor;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;

/** Compatibility icons retained from the newer UI surface that are absent in LowDragLib 1. */
public final class Icons {
    public static final IGuiTexture CLOSE = new TextTexture("×").setColor(0xFFFFFFFF);
    public static final IGuiTexture EXPAND_HORIZONTAL = com.lowdragmc.lowdraglib.gui.editor.Icons.RIGHT;
    public static final IGuiTexture COLLAPSE_HORIZONTAL = com.lowdragmc.lowdraglib.gui.editor.Icons.LEFT;

    private Icons() {
    }
}
