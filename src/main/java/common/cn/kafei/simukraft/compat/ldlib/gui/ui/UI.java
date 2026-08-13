package common.cn.kafei.simukraft.compat.ldlib.gui.ui;

public final class UI {
    private final UIElement root;

    private UI(UIElement root) {
        this.root = root;
    }

    public static UI of(UIElement root) {
        return new UI(root == null ? new UIElement() : root);
    }

    public static UI empty() {
        return of(new UIElement());
    }

    public UIElement root() {
        return root;
    }
}
