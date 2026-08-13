package common.cn.kafei.simukraft.compat.ldlib.editor.ui;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements.Tab;

public class View extends UIElement {
    private final String name;
    private final IGuiTexture icon;
    private boolean canRemove;
    ViewContainer container;

    public View(String name, IGuiTexture icon) {
        this.name = name == null ? "" : name;
        this.icon = icon;
    }

    public String getName() { return name; }
    public IGuiTexture getIcon() { return icon; }
    public boolean canRemove() { return canRemove; }
    public View setCanRemove(boolean value) { canRemove = value; return this; }
    public Tab createTab() { return new Tab().setText(name); }
    protected void onClose() { }

    @Override
    public boolean removeSelf() {
        if (container != null) {
            container.removeView(this);
            return true;
        }
        return super.removeSelf();
    }
}
