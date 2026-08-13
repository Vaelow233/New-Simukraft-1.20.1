package common.cn.kafei.simukraft.compat.ldlib.gui.ui.data;

public final class Transform2D {
    private float translateX;
    private float translateY;

    public static Transform2D identity() {
        return new Transform2D();
    }

    public Transform2D translate(float x, float y) {
        translateX += x;
        translateY += y;
        return this;
    }

    public float translateX() {
        return translateX;
    }

    public float translateY() {
        return translateY;
    }

    public Transform2D copy() {
        return identity().translate(translateX, translateY);
    }
}
