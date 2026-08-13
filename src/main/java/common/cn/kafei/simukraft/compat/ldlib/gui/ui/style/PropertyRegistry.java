package common.cn.kafei.simukraft.compat.ldlib.gui.ui.style;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.Transform2D;

public final class PropertyRegistry {
    public static final Property<Transform2D> TRANSFORM_2D = new Property<>();

    private PropertyRegistry() {
    }

    public static final class Property<T> {
        private Property() {
        }
    }
}
