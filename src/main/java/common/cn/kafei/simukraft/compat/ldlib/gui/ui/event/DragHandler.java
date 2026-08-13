package common.cn.kafei.simukraft.compat.ldlib.gui.ui.event;

public final class DragHandler {
    private final Object draggingObject;

    public DragHandler(Object draggingObject) {
        this.draggingObject = draggingObject;
    }

    public Object getDraggingObject() {
        return draggingObject;
    }
}
