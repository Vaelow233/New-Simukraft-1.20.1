package common.cn.kafei.simukraft.compat.ldlib.gui.ui.event;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;

public final class UIEvent {
    public final UIElement target;
    public final double x;
    public final double y;
    public final int button;
    public final double deltaY;
    public final double dragStartX;
    public final double dragStartY;
    public final DragHandler dragHandler;
    public boolean hasHandler = true;
    private boolean propagationStopped;

    public UIEvent(UIElement target, double x, double y, int button, double deltaY,
                   double dragStartX, double dragStartY, DragHandler dragHandler) {
        this.target = target;
        this.x = x;
        this.y = y;
        this.button = button;
        this.deltaY = deltaY;
        this.dragStartX = dragStartX;
        this.dragStartY = dragStartY;
        this.dragHandler = dragHandler;
    }

    public void stopPropagation() {
        propagationStopped = true;
    }

    public void stopImmediatePropagation() {
        propagationStopped = true;
    }

    public boolean isPropagationStopped() {
        return propagationStopped;
    }
}
